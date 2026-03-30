package com.campus.secondhand.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.campus.secondhand.data.model.local.GoodsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoodsDao {
    // 注解直接修饰函数（不要写在函数内部）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoodsList(goodsList: List<GoodsEntity>)

    @Query("SELECT * FROM goods WHERE category = :category OR :category = '全部'")
    fun getGoodsByCategory(category: String): Flow<List<GoodsEntity>>

    // 根据ID查询商品
    @Query("SELECT * FROM goods WHERE goodsId = :goodsId LIMIT 1")
    suspend fun getGoodsById(goodsId: String): GoodsEntity?

    @Query("UPDATE goods SET isSold = :isSold WHERE goodsId = :goodsId")
    suspend fun updateGoodsSoldStatus(goodsId: String, isSold: Boolean): Int
}