package com.campus.secondhand.data.model.remote

import com.campus.secondhand.core.base.BaseResponse
import java.math.BigDecimal

//商品列表响应数据
data class GoodsResponse(
    val data: List<Goods> = emptyList()
): BaseResponse()

//商品详情响应数据
data class GoodsDetailResponse<T>(
    val data: T? = null // 设为可空，避免后端返回null时崩溃
) : BaseResponse()

// 发布商品响应数据
data class PublishGoodsResponse(
    val goodsId: String?     // 发布成功返回商品ID
): BaseResponse()

// 商品实体类
data class Goods(
    val id: String = "",                            // 不可空，商品id
    val goodsName: String = "",                     // 不可空，商品名
    val price: BigDecimal = BigDecimal.ZERO,        // 不可空，商品价格
    val sellerId: String = "",                      // 不可空，卖家id
    val category: String = "",                      // 不可空，商品分类
    val imageUrl: String = "",                      // 不可空，商品图
    val publishTime: String = "",                   // 不可空，发布时间
    val goodsId: String = "",                       // 不可空，商品id
    val sellerName: String = "",                    // 不可空，卖家名
    val description: String = ""                    // 不可空，商品介绍
)