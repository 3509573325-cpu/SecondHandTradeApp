package com.campus.secondhand.data.local

import com.campus.secondhand.data.model.local.GoodsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoodsLocalDataSource @Inject constructor(
    private val goodsDao: GoodsDao
) {
    suspend fun insertGoodsList(goodsList: List<GoodsEntity>) {
        goodsDao.insertGoodsList(goodsList)
    }

    fun getGoodsByCategory(category: String): Flow<List<GoodsEntity>> {
        return goodsDao.getGoodsByCategory(category)
    }

    suspend fun getGoodsById(goodsId: String): GoodsEntity? {
        return goodsDao.getGoodsById(goodsId)
    }

    suspend fun updateGoodsSoldStatus(goodsId: String, isSold: Boolean): Int {
        return goodsDao.updateGoodsSoldStatus(goodsId, isSold)
    }
}