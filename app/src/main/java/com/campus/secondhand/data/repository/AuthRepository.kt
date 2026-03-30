package com.campus.secondhand.data.repository

import com.campus.secondhand.core.network.NetworkResult
import com.campus.secondhand.data.local.AuthLocalDataSource
import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.model.local.toEntity
import com.campus.secondhand.data.model.remote.LoginRequest
import com.campus.secondhand.data.model.remote.LoginResponse
import com.campus.secondhand.data.model.remote.RegisterRequest
import com.campus.secondhand.data.model.remote.RegisterResponse
import com.campus.secondhand.data.remote.AuthRemoteDataSource
import javax.inject.Inject

/**
 * 认证相关数据仓库（登录/注册）
 * 职责：调度网络和本地数据源，处理数据转换
 */
class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource, // 网络数据源（Hilt注入）
    private val userLocalDataSource: UserLocalDataSource,
    private val authLocalDataSource: AuthLocalDataSource    // 本地数据源（Hilt注入）
) {
    //登录
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = authRemoteDataSource.login(request)
        // 登录成功后同步到Room
        response.data?.let { userData ->
            try {
                if (response.code == 200 && response.data != null) {
                    val userEntity = userData.toEntity(isLogin = true)
                    authLocalDataSource.saveUser(userEntity)
                    NetworkResult.Success(response)
                } else {
                    NetworkResult.Error(response.msg ?: "登录失败", response.code)
                }
            }
            catch (e: Exception) {
                NetworkResult.Error("网络请求失败：${e.message ?: "未知错误"}")
            }
        }
        return response
    }

    //注册
    suspend fun register(request: RegisterRequest): RegisterResponse {
        val response = authRemoteDataSource.register(request)
        // 注册成功后同步到Room
        response.data?.let { userData ->
            try {
                if (response.code == 200) {
                    val userEntity = userData.toEntity(isLogin = true)
                    userLocalDataSource.insertUser(userEntity)
                    NetworkResult.Success(response)
                }
                else {
                    NetworkResult.Error(response.msg ?: "注册失败", response.code)
                }
            }catch (e: Exception) {
                NetworkResult.Error("网络请求失败：${e.message ?: "未知错误"}")
            }
        }
        return response
    }
}