package com.campus.secondhand.data.model.remote

//登录请求参数
data class LoginRequest(
    val phone: String,      // 手机号
    val password: String    // 密码
)
// 注册请求参数
data class RegisterRequest(
    val phone: String,        // 手机号
    val password: String,     // 密码
    val userName: String      // 用户名
)