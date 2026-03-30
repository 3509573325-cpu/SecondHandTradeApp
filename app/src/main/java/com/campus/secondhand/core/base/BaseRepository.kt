// com.campus.secondhand.core.base.BaseRepository.kt
package com.campus.secondhand.core.base

import com.campus.secondhand.core.network.NetworkResult

//Repository基类：封装数据层通用逻辑
open class BaseRepository {
    protected suspend fun <T> handleApiCall(apiCall: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.Success(apiCall())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "网络请求失败")
        }
    }
}