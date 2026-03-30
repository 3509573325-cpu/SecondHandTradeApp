package com.campus.secondhand.data.local

import com.campus.secondhand.core.room.AppDatabase
import com.campus.secondhand.data.model.local.UserEntity
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

//认证相关本地数据源职责：仅处理Room数据库操作，无业务逻辑
class AuthLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase // Hilt注入数据库实例
) {
    // 保存用户信息（复用Dao的insertUser，冲突时替换）
    suspend fun saveUser(userEntity: UserEntity) {
        appDatabase.userDao().insertUser(userEntity)
    }

    // 获取当前登录用户（单个)
    suspend fun getCurrentUser(): UserEntity? {
        // 直接调用Dao的挂起方法，无需Flow处理（Dao中getCurrentLoginUser是suspend，返回单个UserEntity?）
        return appDatabase.userDao().getCurrentLoginUser()
    }

    // 清空用户信息（退出登录）
    suspend fun clearUser() {
        // Dao中logout()方法会把所有用户的isLogin置为0，对应「退出登录」逻辑
        appDatabase.userDao().logout()
    }

    // 监听当前登录用户变化（复用Dao的observeCurrentUser）
    // 如果业务需要响应式更新用户信息，可添加这个方法
    fun observeCurrentUser() = appDatabase.userDao().observeCurrentUser()
}