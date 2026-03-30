package com.campus.secondhand.data.remote

import com.campus.secondhand.core.network.ApiService
import com.campus.secondhand.data.model.remote.LoginRequest
import com.campus.secondhand.data.model.remote.LoginResponse
import com.campus.secondhand.data.model.remote.RegisterRequest
import com.campus.secondhand.data.model.remote.RegisterResponse
import javax.inject.Inject

/**
 * 认证相关网络数据源
 * 职责：仅处理网络请求，无业务逻辑
 */
class AuthRemoteDataSource @Inject constructor(
    private val apiService: ApiService // Hilt注入ApiService
) {
    //登录请求
    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return apiService.login(loginRequest)
    }

    //注册请求
    suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        return apiService.register(registerRequest)
    }
}