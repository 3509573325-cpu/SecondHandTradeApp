package com.campus.secondhand.data.model.remote

// 用户数据实体
data class UserData(
    val userId: String,
    val userName: String,
    val phone: String
)

//用户资料数据
data class UserProfileData(
    val userId: String,
    val userName: String,
    val signature: String
)

//用户资料数据
data class UserInfoData(
    val avatarUrl: String,
    val userName: String,
    val signature: String,

    val schoolInfo: String,

    val publishCount: Int = 0,   // 赋默认值，避免空指针
    val soldCount: Int = 0,
    val boughtCount: Int = 0,
    val collectCount: Int = 0,
    val verifyStatus: Int = 0    // 校园认证状态（0=未认证）
)

//校园认证数据
data class SchoolVerifyData(
    val school: String,
    val grade: String,
    val studentId: String,
    val verifyStatus: Int,          // 0=待审核 1=已认证 2=审核失败
    val verifyMsg: String = ""     // 审核备注
)
