package com.campus.secondhand.data.model.ui

import java.math.BigDecimal

data class OrderUiModel(
    val orderNo: String,
    val goodsName: String,
    val goodsImg: String,
    val price: BigDecimal,
    val orderStatus: String, // 状态文字：待发货/已发货/已收货
    val createTime: String,
    val orderId: String,
    val sellerId: String,
    val buyerId: String,
    val rawStatus: Int // 原始状态值，用于逻辑判断
)