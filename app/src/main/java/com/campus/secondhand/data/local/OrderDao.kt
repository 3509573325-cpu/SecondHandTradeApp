package com.campus.secondhand.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.campus.secondhand.data.model.local.OrderEntity
import com.campus.secondhand.data.model.local.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    // 下单：插入订单
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    // 根据买家ID查询订单列表
    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY createTime DESC")
    fun getOrderListByBuyerId(buyerId: String): Flow<List<OrderEntity>>

    // 根据卖家ID查询订单列表（卖家视角）
    @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY createTime DESC")
    fun getOrderListBySellerId(sellerId: String): Flow<List<OrderEntity>>

    // 更新订单状态
    @Update
    suspend fun updateOrder(order: OrderEntity)

    // 根据订单ID查询订单
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    // 根据商品ID查询未完成订单
    @Query("SELECT * FROM orders WHERE goodsId = :goodsId AND orderStatus IN (0,1) LIMIT 1")
    suspend fun getUnfinishedOrderByGoodsId(goodsId: String): OrderEntity?


    @Query("SELECT * FROM orders")
    suspend fun getAllOrder(): List<OrderEntity>
}