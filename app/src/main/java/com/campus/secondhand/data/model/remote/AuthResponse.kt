package com.campus.secondhand.data.model.remote

import com.campus.secondhand.core.base.BaseResponse

// 登录响应数据
data class LoginResponse(
    val data: UserData? // 用户数据（登录成功返回）
): BaseResponse()

// 注册响应数据
data class RegisterResponse(
    val data: UserData?      // 注册成功返回用户数据
): BaseResponse()