package com.campus.secondhand.data.model.remote

import com.campus.secondhand.core.base.BaseResponse

// 更新用户资料请求（用户名+个性签名合并）
data class UpdateUserProfileRequest(
    val userId: String = "",
    val userName: String,        // 用户名（2-10位）
    val signature: String = ""   // 个性签名（0-50位）
)

// 更新用户资料响应
data class UpdateUserProfileResponse(
    val data: UserProfileData?   // 用户资料数据
): BaseResponse()


// 校园认证请求
data class SchoolVerifyRequest(
    val userId: String = "",
    val school: String,          // 学校名称
    val grade: String,           // 年级专业
    val studentId: String        // 学号
)

// 校园认证响应
data class SchoolVerifyResponse(
    val data: SchoolVerifyData?
): BaseResponse()

data class RechargeRequest(
    val userId: String = "",
    val amount: Float,        // 充值金额
    val payType: String = "WECHAT" // 支付类型（WECHAT/ALIPAY）
)

// 用户信息查询响应
data class UserInfoResponse(
    val data: UserInfoData?
): BaseResponse()

