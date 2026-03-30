package com.campus.secondhand.core.network

//网络请求结果密封类，统一处理成功/失败/加载状态
sealed class NetworkResult<out T> {
    object Loading : NetworkResult<Nothing>()
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int = -1) : NetworkResult<Nothing>()
}