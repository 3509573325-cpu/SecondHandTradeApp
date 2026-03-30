package com.campus.secondhand.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.campus.secondhand.data.model.local.UserEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface UserDao {
    // 插入用户（冲突时替换，如登录后更新）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // 更新用户信息（如修改昵称、签名、认证状态）
    @Update
    suspend fun updateUser(user: UserEntity)

    // 根据UserID查询用户
    @Query("SELECT * FROM user WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    // 查询当前登录的用户（仅一个）
    @Query("SELECT * FROM user WHERE isLogin = 1 LIMIT 1")
    suspend fun getCurrentLoginUser(): UserEntity?

    // 监听当前登录用户的信息变化（Flow响应式）
    @Query("SELECT * FROM user WHERE isLogin = 1 LIMIT 1")
    fun observeCurrentUser(): Flow<UserEntity?>

    // 更新用户余额（根据userId）
    @Query("UPDATE user SET balance = :newBalance WHERE userId = :userId")
    suspend fun updateUserBalance(userId: String, newBalance: BigDecimal)

    // 查询当前登录用户的余额（Flow监听）
    @Query("SELECT balance FROM user WHERE isLogin = 1 LIMIT 1")
    fun observeUserBalance(): Flow<BigDecimal?>

    // 获取当前登录用户的余额（单次查询）
    @Query("SELECT balance FROM user WHERE isLogin = 1 LIMIT 1")
    suspend fun getCurrentUserBalance(): BigDecimal?

    // 登出：重置登录状态
    @Query("UPDATE user SET isLogin = 0 WHERE isLogin = 1")
    suspend fun logout()

    // 删除用户（清空本地数据）
    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<UserEntity>

}