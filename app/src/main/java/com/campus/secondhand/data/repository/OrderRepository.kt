package com.campus.secondhand.data.repository

import android.util.Log
import com.campus.secondhand.data.local.OrderDao
import com.campus.secondhand.data.local.GoodsDao
import com.campus.secondhand.data.local.GoodsLocalDataSource
import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.model.local.OrderEntity
import com.campus.secondhand.data.model.ui.OrderUiModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class OrderRepository @Inject constructor(
    private val goodsLocalDataSource: GoodsLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val orderDao: OrderDao,
) {
    // 生成唯一订单号
    private fun generateOrderNo(): String {
        return "CSH${System.currentTimeMillis()}${UUID.randomUUID().toString().substring(0, 6)}"
    }

    // 下单核心逻辑：扣买家余额 → 创建订单 → 标记商品为售出
    suspend fun createOrder(
        goodsId: String,
        buyerId: String
    ): Boolean {
        // 1. 查询商品信息
        val goods = goodsLocalDataSource.getGoodsById(goodsId) ?: return false
        // 2. 校验商品是否已售出
        if (goods.isSold) return false
        // 3. 查询买家信息
        val buyer = userLocalDataSource.getUserById(buyerId) ?: return false
        // 4. 校验买家余额是否足够
        if (buyer.balance < goods.price) return false

        // 5. 扣减买家余额
        val newBuyerBalance = buyer.balance.subtract(goods.price)
        userLocalDataSource.updateUser(buyer.copy(balance = newBuyerBalance))

        // 6. 创建订单
        val order = OrderEntity(
            orderNo = generateOrderNo(),
            goodsId = goodsId,
            goodsName = goods.goodsName,
            price = goods.price,
            goodsImg = goods.imageUrl,
            buyerId = buyerId,
            sellerId = goods.sellerId,
            createTime = System.currentTimeMillis(),
            orderStatus = 0 // 待发货
        )
        orderDao.insertOrder(order)
        val allOrder = orderDao.getAllOrder()
        Log.d("orders", "所有订单：$allOrder")
        // 7. 标记商品为售出
        goodsLocalDataSource.updateGoodsSoldStatus(goodsId ,true )

        return true
    }

    // 卖家确认发货
    suspend fun confirmShip(orderId: String): Boolean {
        val order = orderDao.getOrderById(orderId) ?: return false
        // 仅待发货状态可发货
        if (order.orderStatus != 0) return false
        val updatedOrder = order.copy(
            orderStatus = 1, // 已发货
            shipTime = System.currentTimeMillis()
        )
        orderDao.updateOrder(updatedOrder)
        return true
    }

    // 买家确认收货：将款项转给卖家
    suspend fun confirmReceive(orderId: String): Boolean {
        val order = orderDao.getOrderById(orderId) ?: return false
        // 仅已发货状态可确认收货
        if (order.orderStatus != 1) return false

        // 1. 查询卖家信息
        val seller = userLocalDataSource.getUserById(order.sellerId) ?: return false
        // 2. 增加卖家余额
        val newSellerBalance = seller.balance.add(order.price)
        userLocalDataSource.updateUser(seller.copy(balance = newSellerBalance))

        // 3. 更新订单状态
        val updatedOrder = order.copy(
            orderStatus = 2, // 已收货
            receiveTime = System.currentTimeMillis()
        )
        orderDao.updateOrder(updatedOrder)
        return true
    }

    // 获取买家订单列表（转为UI模型）
    fun getBuyerOrderList(buyerId: String): Flow<List<OrderUiModel>> {
        return orderDao.getOrderListByBuyerId(buyerId)
            .map { orderList ->
                orderList.map { order ->
                    OrderUiModel(
                        orderNo = order.orderNo,
                        goodsName = order.goodsName,
                        goodsImg = order.goodsImg,
                        price = order.price,
                        orderStatus = when (order.orderStatus) {
                            0 -> "待发货"
                            1 -> "已发货"
                            2 -> "已收货"
                            else -> "已取消"
                        },
                        createTime = order.createTime.toString(), // 实际项目需格式化
                        orderId = order.id,
                        sellerId = order.sellerId,
                        buyerId = order.buyerId,
                        rawStatus = order.orderStatus
                    )
                }
            }
    }

    // 获取卖家订单列表（转为UI模型）
    fun getSellerOrderList(sellerId: String): Flow<List<OrderUiModel>> {
        return orderDao.getOrderListBySellerId(sellerId)
            .map { orderList ->
                orderList.map { order ->
                    OrderUiModel(
                        orderNo = order.orderNo,
                        goodsName = order.goodsName,
                        goodsImg = order.goodsImg,
                        price = order.price,
                        orderStatus = when (order.orderStatus) {
                            0 -> "待发货"
                            1 -> "已发货"
                            2 -> "已收货"
                            else -> "已取消"
                        },
                        createTime = order.createTime.toString(),
                        orderId = order.id,
                        sellerId = order.sellerId,
                        buyerId = order.buyerId,
                        rawStatus = order.orderStatus
                    )
                }
            }
    }
}