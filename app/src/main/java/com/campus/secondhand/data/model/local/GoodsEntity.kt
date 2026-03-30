package com.campus.secondhand.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "goods")
data class GoodsEntity(
    @PrimaryKey val goodsId: String,
    val goodsName: String,    //商品名
    val price: BigDecimal,    //商品价格
    val category: String,     //商品分类
    val description: String,  //商品简介
    val sellerId: String,     //卖家id
    val sellerName: String,   //卖家名
    val publishTime: String,  //发布时间
    val imageUrl: String,     //商品图
    val isSold: Boolean = false
)