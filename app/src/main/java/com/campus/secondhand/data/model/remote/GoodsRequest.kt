package com.campus.secondhand.data.model.remote

// 发布商品请求参数
data class PublishGoodsRequest(
    val goodsName: String,        // 商品名称
    val price: Float,             // 商品价格
    val description: String,      // 商品描述
    val sellerId: String,         // 卖家ID（登录后从本地获取）
    val sellerName: String,       // 卖家名称（登录后从本地获取）
    val shipType: String,         // 发货方式
    val location: String,         // 所在位置
    val category: String,         // 商品分类
    val imageUrl: String,         // 商品图片URL（后端返回的访问地址）
)