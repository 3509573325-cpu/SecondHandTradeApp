// com.campus.secondhand.data.repository.GoodsRepository.kt
package com.campus.secondhand.data.repository

import com.campus.secondhand.core.base.BaseRepository
import com.campus.secondhand.core.network.ApiService
import com.campus.secondhand.core.network.NetworkResult
import com.campus.secondhand.data.model.remote.Goods
import com.campus.secondhand.data.model.remote.GoodsDetailResponse
import com.campus.secondhand.data.model.remote.GoodsResponse
import com.campus.secondhand.data.model.remote.PublishGoodsRequest
import com.campus.secondhand.data.model.remote.PublishGoodsResponse
import javax.inject.Inject

//商品仓库：统一管理商品相关的网络/本地数据
class GoodsRepository @Inject constructor(
    private val apiService: ApiService // Hilt注入ApiService，替代手动创建
) : BaseRepository() {

    // 获取商品列表
    suspend fun getGoodsList(): NetworkResult<GoodsResponse> {
        return handleApiCall {
            apiService.getGoodsList()
        }
    }
    //商品详情
    suspend fun getGoodsById(goodsId: String): NetworkResult<GoodsDetailResponse<Goods>> {
        return handleApiCall {
            apiService.getGoodsDetail(goodsId)
        }
    }

    // 发布商品
    suspend fun publishGoods(request: PublishGoodsRequest): NetworkResult<PublishGoodsResponse> {
        return handleApiCall {
            apiService.publishGoods(request)
        }
    }
}