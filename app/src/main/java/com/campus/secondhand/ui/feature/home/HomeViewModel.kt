package com.campus.secondhand.ui.feature.home

import androidx.lifecycle.viewModelScope
import com.campus.secondhand.core.base.BaseViewModel
import com.campus.secondhand.core.network.NetworkResult
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.data.local.GoodsLocalDataSource
import com.campus.secondhand.data.model.local.GoodsEntity
import com.campus.secondhand.data.model.remote.Goods
import com.campus.secondhand.data.model.ui.GoodsCategory
import com.campus.secondhand.data.repository.GoodsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//首页ViewModel：仅处理UI状态和调用Repository，无直接网络/数据库操作
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val goodsRepository: GoodsRepository,
    private val goodsLocalDataSource: GoodsLocalDataSource
) : BaseViewModel() {
    // 商品列表UI状态（单一数据源）
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()
    //搜索关键词
    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword.asStateFlow()
    private val _currentCategory = MutableStateFlow(GoodsCategory.ALL.categoryName)
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    // 初始化：只启动一次防抖搜索（核心修复：避免重复协程）
    init {
        viewModelScope.launch {
            _searchKeyword
                .debounce(500) // 500ms防抖
                .distinctUntilChanged()
                .collect { filterGoods() }
        }
    }

    // 处理搜索输入（仅更新关键词，筛选逻辑在init的协程中）
    fun handleSearch(keyword: String) {
        _searchKeyword.value = keyword.trim()
    }
    // 切换分类（新增：和UI层联动）
    fun switchCategory(categoryName: String) {
        _currentCategory.value = categoryName
        filterGoods() // 切换分类立即筛选
    }

    //加载商品列表（支持主动刷新）空值兜底 + Room缓存 + 错误提示
    fun loadGoodsList(context: android.content.Context) {
        launch {
            _homeUiState.update { it.copy(isLoading = true) } // 改用update避免状态覆盖
            when (val result = goodsRepository.getGoodsList()) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.code == 200) {
                        val list = response.data.map { rawGoods ->
                            rawGoods.copy(
                                goodsName = rawGoods.goodsName.ifBlank { "未命名商品" },
                                publishTime = rawGoods.publishTime.ifBlank { "未知时间" },
                                imageUrl = rawGoods.imageUrl.ifBlank { "https://placeholder.pics/svg/100x100/EEEEEE/999999/暂无图片" },
                                description = rawGoods.description.ifBlank { "无描述" },
                            )
                        }
                        // 先更新原始列表，再筛选，最后重置加载状态
                        _homeUiState.update {
                            it.copy(
                                goodsList = list,
                                isLoading = false // 加载完成立即重置
                            )
                        }
                        filterGoods() // 筛选后更新displayGoodsList
                        cacheGoodsToLocal(list)
                    } else {
                        ToastUtils.show(context, response.msg ?: "加载商品失败")
                        _homeUiState.update { it.copy(isLoading = false) }
                    }
                }
                is NetworkResult.Error -> {
                    setErrorMsg(result.message ?: "网络异常，请重试")
                    ToastUtils.show(context, result.message ?: "网络异常，请重试")
                    _homeUiState.update { it.copy(isLoading = false) }
                }
                NetworkResult.Loading -> {}
            }
        }
    }

    //缓存商品数据到本地数据库
    private suspend fun cacheGoodsToLocal(goodsList: List<Goods>) {
        val goodsEntities = goodsList.map {
            GoodsEntity(
                goodsId = it.goodsId.ifBlank { it.id },
                goodsName = it.goodsName.ifBlank { "未命名商品" },
                imageUrl = it.imageUrl.ifBlank { "https://placeholder.pics/svg/200x200/EEEEEE/999999/暂无图片" },
                price = it.price,
                description = it.description.ifBlank { "无描述" },
                sellerId = it.sellerId.ifBlank { "未知ID" },
                sellerName = it.sellerName.ifBlank { "未知卖家" },
                publishTime = it.publishTime.ifBlank { "未知时间" },
                category = it.category.ifBlank { "其他" }
            )
        }
        goodsLocalDataSource.insertGoodsList(goodsEntities)
    }

    //筛选商品
    private fun filterGoods() {
        val originalList = _homeUiState.value.goodsList
        val keyword = _searchKeyword.value.trim().lowercase()
        val category = _currentCategory.value.lowercase()

        // 1. 分类筛选（空值兜底 + 大小写不敏感）
        var result = if (category == GoodsCategory.ALL.categoryName.lowercase() || category.isBlank()) {
            originalList
        } else {
            originalList.filter {
                it.category.lowercase().contains(category) || it.category.isBlank() && category == "其他"
            }
        }

        // 2. 搜索筛选（关键词非空时才过滤）
        if (keyword.isNotEmpty()) {
            result = result.filter {
                it.goodsName.lowercase().contains(keyword) ||
                        it.description.lowercase().contains(keyword) ||
                        it.sellerName.lowercase().contains(keyword)
            }
        }

        // 最终更新展示列表
        _homeUiState.update {
            it.copy(displayGoodsList = result)
        }
    }

    // 清空搜索（修复：清空后重新筛选）
    fun clearSearch() {
        _searchKeyword.value = ""
        filterGoods() // 清空后恢复分类筛选结果
    }
}