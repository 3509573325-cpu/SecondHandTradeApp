package com.campus.secondhand.data.model.local

import java.math.BigDecimal
import androidx.room.Entity
import androidx.room.PrimaryKey

// Room用户表实体（主键为userId）
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val userId: String,          // 用户唯一标识（核心）
    val userName: String,                    // 用户名
    val phone: String,                       // 手机号
    val isLogin: Boolean = false,              // 登录状态标记
    val avatarUrl: String = "",              // 头像URL
    val signature: String = "",              // 个性签名
    val schoolInfo: String = "",             // 校园信息
    val studentId: String = "",              // 学号
    val grade: String = "",                  // 年级专业
    val verifyStatus: Int = 0,               // 校园认证状态（0未认证/1已认证/2审核失败）
    val publishCount: Int = 0,               // 发布商品数
    val soldCount: Int = 0,                  // 售出商品数
    val boughtCount: Int = 0,                // 购买商品数
    val collectCount: Int = 0,               // 收藏商品数
    var balance: BigDecimal = BigDecimal.ZERO,//用户余额
)

// 扩展方法：网络模型转本地实体
fun com.campus.secondhand.data.model.remote.UserInfoData.toEntity(userId: String): UserEntity {
    return UserEntity(
        userId = userId,
        userName = this.userName,
        phone = "", // 需从登录/注册返回的UserData中补充
        avatarUrl = this.avatarUrl,
        signature = this.signature,
        schoolInfo = this.schoolInfo,
        verifyStatus = this.verifyStatus,
        publishCount = this.publishCount,
        soldCount = this.soldCount,
        boughtCount = this.boughtCount,
        collectCount = this.collectCount
    )
}

// 扩展方法：登录返回的UserData转本地实体
fun com.campus.secondhand.data.model.remote.UserData.toEntity(isLogin: Boolean = true): UserEntity {
    return UserEntity(
        userId = this.userId,
        userName = this.userName,
        phone = this.phone,
        isLogin = isLogin
    )
}