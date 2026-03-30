package com.campus.secondhand.data.local

import com.campus.secondhand.data.model.local.UserEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

// 实现类
class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao
){
    suspend fun getCurrentLoginUser(): UserEntity? {
        return try {
            userDao.getCurrentLoginUser()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun logout() {
        userDao.logout()
    }

    suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun updateUserBalance(userId: String, newBalance: BigDecimal) {
        userDao.updateUserBalance(userId, newBalance)
    }

    // 监听当前登录用户的余额变化
    fun observeUserBalance(): Flow<BigDecimal?> {
        return userDao.observeUserBalance()
    }

    // 获取当前登录用户的余额
    suspend fun getCurrentUserBalance(): BigDecimal? {
        return userDao.getCurrentUserBalance()
    }

    fun observeCurrentUser(): Flow<UserEntity?> {
        return userDao.observeCurrentUser()
    }
    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAllUsers()
    }

}