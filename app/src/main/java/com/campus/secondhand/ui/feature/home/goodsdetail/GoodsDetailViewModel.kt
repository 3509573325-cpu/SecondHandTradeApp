package com.campus.secondhand.ui.feature.home.goodsdetail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.campus.secondhand.core.base.BaseViewModel
import com.campus.secondhand.core.network.NetworkResult
import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.repository.GoodsRepository
import com.campus.secondhand.data.repository.OrderRepository
import com.campus.secondhand.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoodsDetailViewModel @Inject constructor(
    private val goodsRepository: GoodsRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val userLocalDataSource: UserLocalDataSource
) : BaseViewModel() {

    // UI状态（私有可变，公有只读）
    private val _uiState = MutableStateFlow(GoodsDetailUiState())
    val uiState: StateFlow<GoodsDetailUiState> = _uiState.asStateFlow()

    // 下单结果状态：0-初始 1-成功 2-失败（余额不足）3-失败（商品已售）4-未登录
    private val _orderResult = MutableStateFlow<Int>(0)
    val orderResult: StateFlow<Int> = _orderResult
    /**
     * 根据商品ID加载详情
     * @param goodsId 商品ID
     * @param context 上下文（用于Toast提示）
     */
    fun loadGoodsDetail(goodsId: String, context: Context) {
        // 重置状态
        _uiState.update { it.copy(isLoading = true, errorMsg = "") }

        viewModelScope.launch {
            when (val result = goodsRepository.getGoodsById(goodsId)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.code == 200) {
                        // 空值兜底处理（与首页保持一致）
                        val goods = response.data?.copy(
                            goodsName = response.data.goodsName.ifBlank { "未命名商品" },
                            publishTime = response.data.publishTime.ifBlank { "未知时间" },
                            imageUrl = response.data.imageUrl.ifBlank { "https://placeholder.pics/svg/300x300/EEEEEE/999999/暂无图片" },
                            description = response.data.description.ifBlank { "无描述" },
                            sellerName = response.data.sellerName.ifBlank { "未知卖家" },
                            category = response.data.category.ifBlank { "其他" },
                           // shipType = response.data.shipType.ifBlank { "未设置" },
                          //  location = response.data.location.ifBlank { "未知位置" }
                        )
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                goods = goods
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMsg = response.msg ?: "加载商品详情失败"
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = result.message ?: "网络异常"
                        )
                    }
                }
                NetworkResult.Loading -> {}
            }
        }
    }

    // 下单操作
    fun createOrder(goodsId: String) {
        viewModelScope.launch {
            // 1. 校验登录状态
            val currentUser = userLocalDataSource.getCurrentLoginUser() ?: run {
                _orderResult.value = 4
                return@launch
            }

            // 2. 调用仓库下单逻辑
            val result = orderRepository.createOrder(goodsId, currentUser.userId)
            if (result) {
                _orderResult.value = 1 // 下单成功
            } else {
                // 可细化失败原因，此处简化为余额不足/商品已售
                _orderResult.value = 2
            }
        }
    }

    // 重置下单状态
    fun resetOrderResult() {
        _orderResult.value = 0
    }
}