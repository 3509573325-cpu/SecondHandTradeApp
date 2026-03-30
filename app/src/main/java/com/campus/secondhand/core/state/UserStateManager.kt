package com.campus.secondhand.core.state

import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStateManager @Inject constructor(
    private val userRepository: UserRepository, // 注入仓库，关联Room
    private val userLocalDataSource: UserLocalDataSource
) {
    private val _isLogin = MutableStateFlow(false)
    val isLogin: StateFlow<Boolean> = _isLogin.asStateFlow()

    // 初始化：从Room读取登录状态
    init {
        runBlocking {
            _isLogin.value = userLocalDataSource.getCurrentLoginUser() != null
        }
    }

    // 标记为已登录（同步Room状态）
    fun markAsLoggedIn() {
        _isLogin.value = true

    }

    // 标记为未登录（同步Room状态）
    suspend fun markAsLoggedOut() {
        _isLogin.value = false
        userRepository.logout() // 调用Room的登出方法
    }

    // 检查是否登录（优先读Room）
    suspend fun checkIsLogin(): Boolean {
        val currentUser = userLocalDataSource.getCurrentLoginUser()
        _isLogin.value = currentUser != null
        return _isLogin.value
    }
}