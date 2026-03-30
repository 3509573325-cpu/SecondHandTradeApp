package com.campus.secondhand.data.repository

import android.util.Log
import com.campus.secondhand.core.network.ApiService
import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.model.local.toEntity
import com.campus.secondhand.data.model.remote.SchoolVerifyRequest
import com.campus.secondhand.data.model.remote.SchoolVerifyResponse
import com.campus.secondhand.data.model.remote.UpdateUserProfileRequest
import com.campus.secondhand.data.model.remote.UpdateUserProfileResponse
import com.campus.secondhand.data.model.remote.UploadImageResponse
import com.campus.secondhand.data.model.remote.UserInfoData
import com.campus.secondhand.data.model.remote.UserInfoResponse
import okhttp3.MultipartBody
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

//用户数据仓库：优先读取Room本地数据，网络请求后同步到本地
@Singleton // 标记为单例，与Hilt的@Singleton匹配
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userLocalDataSource: UserLocalDataSource
) {
    // 监听当前用户信息变化
    fun observeCurrentUser() = userLocalDataSource.observeCurrentUser()
    fun observeUserBalance() = userLocalDataSource.observeUserBalance()
    // 登出（本地状态重置）
    suspend fun logout() {
        try {
            userLocalDataSource.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 查询用户信息：优先读本地，本地无则请求网络并同步
    suspend fun getUserInfo(): UserInfoResponse {
            val allUsers = userLocalDataSource.getAllUsers()
            Log.d("UserDebug", "所有用户：$allUsers") // 查看是否有isLogin=true的用户
        // 1. 获取本地登录用户的UserID
        val currentUser = userLocalDataSource.getCurrentLoginUser()
            ?: throw IllegalStateException("用户未登录，请先登录")

        // 2. 优先返回本地数据
        val localUser = userLocalDataSource.getUserById(currentUser.userId)
        if (localUser != null) {
            // 转换为网络响应模型
            val userInfoData = UserInfoData(
                avatarUrl = localUser.avatarUrl,
                userName = localUser.userName,
                signature = localUser.signature,
                schoolInfo = localUser.schoolInfo,
                publishCount = localUser.publishCount,
                soldCount = localUser.soldCount,
                boughtCount = localUser.boughtCount,
                collectCount = localUser.collectCount,
                verifyStatus = localUser.verifyStatus
            )
            return UserInfoResponse(data = userInfoData)
        }

        // 3. 本地无数据，请求网络并同步到Room
        val response = apiService.getUserInfo(currentUser.userId)
        response.data?.let { userInfoData ->
            try {
                val userEntity = userInfoData.toEntity(currentUser.userId)
                userLocalDataSource.updateUser(userEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }

    // 更新用户资料：先请求网络，成功后同步到Room
    suspend fun updateUserProfile(request: UpdateUserProfileRequest): UpdateUserProfileResponse {
        // 1. 获取本地UserID
        val currentUser = userLocalDataSource.getCurrentLoginUser()
            ?: throw IllegalStateException("用户未登录，请先登录")

        // 2. 补充UserID到请求
        val requestWithUserId = request.copy(userId = currentUser.userId)

        // 3. 网络请求
        val response = apiService.updateUserProfile(requestWithUserId)

        // 4. 同步到Room
        response.data?.let { profileData ->
            try {
                val updatedUser = currentUser.copy(
                    userName = profileData.userName,
                    signature = profileData.signature
                )
                userLocalDataSource.updateUser(updatedUser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }

    // 校园认证：网络请求后同步到Room
    suspend fun submitSchoolVerify(request: SchoolVerifyRequest): SchoolVerifyResponse {
        val currentUser = userLocalDataSource.getCurrentLoginUser()
            ?: throw IllegalStateException("用户未登录，请先登录")

        // 补充UserID到认证请求
        val requestWithUserId = request.copy(userId = currentUser.userId)

        val response = apiService.submitSchoolVerify(requestWithUserId)
        // 同步认证状态到本地
        response.data?.let { verifyData ->
            try {
                val updatedUser = currentUser.copy(
                    schoolInfo = "${verifyData.school}-${verifyData.grade}",
                    studentId = verifyData.studentId,
                    verifyStatus = verifyData.verifyStatus
                )
                userLocalDataSource.updateUser(updatedUser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }

    // 上传头像：网络请求后同步到Room
    suspend fun uploadAvatar(imagePart: MultipartBody.Part): UploadImageResponse {
        val currentUser = userLocalDataSource.getCurrentLoginUser()
            ?: throw IllegalStateException("用户未登录，请先登录")

        val response = apiService.uploadImage(imagePart)
        // 同步头像URL到本地
        response.data?.imageUrl?.let { avatarUrl ->
            try {
                val updatedUser = currentUser.copy(avatarUrl = avatarUrl)
                userLocalDataSource.updateUser(updatedUser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }

     suspend fun rechargeUserBalance(userId: String, amount: BigDecimal): Boolean {
        return try {
            val currentBalance = userLocalDataSource.getCurrentUserBalance() ?: BigDecimal.ZERO
            val newBalance = currentBalance.add(amount)
            userLocalDataSource.updateUserBalance(userId, newBalance)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getCurrentLoginUserId(): String {
        val currentUser = userLocalDataSource.getCurrentLoginUser()
            ?: throw IllegalStateException("用户未登录，请先登录")
        return currentUser.userId
    }

    // 获取当前余额
     suspend fun getCurrentUserBalance(): BigDecimal? {
        return userLocalDataSource.getCurrentUserBalance()
    }
}