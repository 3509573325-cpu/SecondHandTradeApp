package com.campus.secondhand.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.UUID

//订单数据库实体
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val orderNo: String, // 订单编号（唯一）
    val goodsId: String, // 商品ID
    val goodsName: String, // 商品名称
    val price: BigDecimal, // 商品价格
    val goodsImg: String, // 商品封面图
    val buyerId: String, // 买家ID
    val sellerId: String, // 卖家ID
    var orderStatus: Int, // 订单状态：0-待发货 1-已发货 2-已收货 3-已取消
    val createTime: Long, // 下单时间
    var receiveTime: Long? = null, // 确认收货时间
    var shipTime: Long? = null // 发货时间
)