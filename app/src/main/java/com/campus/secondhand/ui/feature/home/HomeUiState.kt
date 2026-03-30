// com.campus.secondhand.ui.feature.home.HomeUiState.kt
package com.campus.secondhand.ui.feature.home

import com.campus.secondhand.data.model.remote.Goods

//首页UI状态封装（单一数据源）
data class HomeUiState(
    val isLoading: Boolean = false,
    val goodsList: List<Goods> = emptyList(),       // 原始商品列表
    val displayGoodsList: List<Goods> = emptyList(), // 展示列表
    val searchKeyword: String = "",                 // 搜索关键词
)