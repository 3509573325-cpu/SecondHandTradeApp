package com.campus.secondhand.ui.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.data.model.remote.Goods
import com.campus.secondhand.data.model.ui.GoodsCategory
import java.math.BigDecimal

//首页核心UI（纯展示）
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onGoodsClick: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // 状态收集
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val errorMsg by viewModel.errorMsg.collectAsStateWithLifecycle()
    val searchKeyword by viewModel.searchKeyword.collectAsStateWithLifecycle()
    val currentCategory by viewModel.currentCategory.collectAsStateWithLifecycle()
    // 选中分类的本地状态
    var selectedCategory by remember { mutableStateOf(GoodsCategory.ALL) }

    // 初始化加载
    LaunchedEffect(Unit) {
        viewModel.loadGoodsList(context)
    }

    // 错误提示
    LaunchedEffect(errorMsg) {
        if (errorMsg.isNotEmpty()) {
            ToastUtils.show(context, errorMsg)
            viewModel.clearErrorMsg()
        }
    }

    // 分类筛选联动
//    LaunchedEffect(selectedCategory) {
//        viewModel.switchCategory(selectedCategory.categoryName)
//    }
    LaunchedEffect(currentCategory) {
        // 从ViewModel同步选中状态到UI
        GoodsCategory.entries.find { it.categoryName == currentCategory }?.let {
            selectedCategory = it
        }
    }

    // 加载弹窗
    if (homeUiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("加载中...")
        }
    }

    // 核心布局
    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索框
        GoodsSearchBar(
            keyword = searchKeyword,
            onKeywordChange = { viewModel.handleSearch(it) },
            onClearClick = { viewModel.clearSearch() },
            modifier = Modifier.fillMaxWidth()
        )

        // 商品分类栏（横向流式布局）
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            GoodsCategory.entries.forEach { category ->
                val onSelect: (GoodsCategory) -> Unit = remember(category) {
                    { selectedCategory = category }
                }
                CategoryTag(
                    category = category,
                    isSelected = category == selectedCategory,
                    onSelect = onSelect
                )
            }
        }

        // 商品列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 60.dp
            )
        ) {
            itemsIndexed(homeUiState.displayGoodsList) { _, goods ->
                GoodsItemCard(
                    goods = goods,
                    onItemClick = { onGoodsClick(goods.goodsId) }
                )
            }
        }
    }
}

@Composable
fun GoodsSearchBar(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = keyword,
        onValueChange = onKeywordChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(text = "搜索商品名称/描述...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
        trailingIcon = {
            if (keyword.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Default.Clear, contentDescription = "清空")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { /* 软键盘搜索按钮触发，此处防抖已处理，无需额外操作 */ }
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.LightGray,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

//分类标签通用组件
@Composable
fun CategoryTag(
    category: GoodsCategory,
    isSelected: Boolean,
    onSelect: (GoodsCategory) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.LightGray.copy(alpha = 0.5f)
            )
            .clickable { onSelect(category) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.categoryName,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

//商品项卡片UI
@Composable
fun GoodsItemCard(
    goods: Goods,
    onItemClick: (String) -> Unit
) {
    // 空值兜底处理
    val goodsTitle = goods.goodsName.ifBlank { "未命名商品" }
    val publishTime = goods.publishTime.ifBlank { "未知时间" }
    val sellerName = goods.sellerName.ifBlank { "未知ID" }
    val description = goods.description.ifBlank { "无描述" }
    val goodsId = goods.goodsId.ifBlank {
        // 打印日志，定位空值商品
        android.util.Log.e("GoodsItemCard", "商品goodsId为空，商品名称：${goods.goodsName}")
        ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onItemClick(goodsId)  },
        shape = RoundedCornerShape(CornerSize(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 商品图片
            Image(
                painter = rememberAsyncImagePainter(
                    model = goods.imageUrl.ifBlank { "https://placeholder.pics/svg/100x100/EEEEEE/999999/暂无图片" }
                ),
                contentDescription = goodsTitle,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 商品信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = goodsTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "¥${goods.price.takeIf { it > BigDecimal.ZERO  } ?: "0.00"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "卖家：$sellerName",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = publishTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// 预览
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}