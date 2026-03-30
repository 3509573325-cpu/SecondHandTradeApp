package com.campus.secondhand.ui

import androidx.compose.runtime.mutableStateOf

// 全局登录状态（仅这1个变量，无其他代码）
object AppState {
    var isLoggedIn = mutableStateOf(false)
}