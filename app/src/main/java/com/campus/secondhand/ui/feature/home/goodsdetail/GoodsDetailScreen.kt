package com.campus.secondhand.ui.feature.home.goodsdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.data.model.remote.Goods
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsDetailScreen(
    goodsId: String,          // 接收商品ID参数
    onBackClick: () -> Unit,  // 返回按钮回调
    onOrderClick: (String) -> Unit = {},
    viewModel: GoodsDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // 收集UI状态
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // 初始化加载商品详情
    LaunchedEffect(goodsId) {
        viewModel.loadGoodsDetail(goodsId, context)
    }

    // 错误提示
    LaunchedEffect(uiState.errorMsg) {
        if (uiState.errorMsg.isNotEmpty()) {
            ToastUtils.show(context, uiState.errorMsg)
            viewModel.clearErrorMsg()
        }
    }

    // 页面主体布局
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("商品详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 加载中状态
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }
            } else {
                // 商品详情内容
                uiState.goods?.let { goods ->
                    GoodsDetailContent(
                        goods = goods,
                        scrollState = scrollState,
                                onOrderClick = {
                            // 点击下单时调用ViewModel的下单逻辑
                            viewModel.createOrder(goods.goodsId)
                        }
                    )
                } ?: run {
                    // 空数据状态
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无商品信息",
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

//商品详情内容区域
@Composable
private fun GoodsDetailContent(
    goods: Goods,
    scrollState: ScrollState,
    onOrderClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. 商品主图
        Image(
            painter = rememberAsyncImagePainter(
                model = goods.imageUrl,
                error = rememberAsyncImagePainter("https://placeholder.pics/svg/300x300/EEEEEE/999999/暂无图片")
            ),
            contentDescription = goods.goodsName,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )

        // 2. 商品基础信息
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // 商品名称
            Text(
                text = goods.goodsName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 商品价格
            Text(
                text = "¥${goods.price.takeIf { it > BigDecimal.ZERO} ?: "0.00"}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 卖家信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "卖家：${goods.sellerName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "发布时间：${goods.publishTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 分割线
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 商品分类
            DetailInfoItem(
                label = "商品分类",
                value = goods.category
            )

//            // 发货方式
//            DetailInfoItem(
//                label = "发货方式",
//                value = goods.shipType
//            )
//
//            // 所在位置
//            DetailInfoItem(
//                label = "所在位置",
//                value = goods.location
//            )

            Spacer(modifier = Modifier.height(16.dp))

            // 分割线
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 商品描述
            Text(
                text = "商品描述",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = goods.description,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 24.sp
            )
        }
        Button(
            onClick = onOrderClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "立即下单")
        }
        // 底部留白
        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * 详情页信息项通用组件（标签+值）
 */
@Composable
private fun DetailInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label：",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// 预览
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GoodsDetailScreenPreview() {
    val mockGoods = Goods(
        goodsId = "123",
        goodsName = "二手小米14手机",
        price = BigDecimal.ZERO,
        description = "99新小米14，全套配件，无拆无修，电池健康度98%，自用半年，因换手机出售。",
        sellerName = "小明",
        publishTime = "2024-05-20",
        imageUrl = "",
        category = "数码产品",
//        shipType = "自提",
//        location = "弦高西湖凶文旅街区"
    )
    Column(modifier = Modifier.fillMaxSize()) {
        GoodsDetailContent(
            goods = mockGoods,
            scrollState = ScrollState(0),
            onOrderClick = {}
        )
    }
}