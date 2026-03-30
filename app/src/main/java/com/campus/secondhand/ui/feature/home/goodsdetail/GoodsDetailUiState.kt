package com.campus.secondhand.ui.feature.home.goodsdetail

import com.campus.secondhand.data.model.remote.Goods

// 商品详情页UI状态（封装所有UI所需状态）
data class GoodsDetailUiState(
    val isLoading: Boolean = false,          // 数据加载中
    val goods: Goods? = null,                // 商品详情数据
    val errorMsg: String = ""                // 错误提示信息
)