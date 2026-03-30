package com.campus.secondhand.ui.feature.user

// 个人中心UI状态封装
data class UserUiState(
    // 用户基础信息
    val userId: String = "",
    val avatarUrl: String = "https://placeholder.pics/svg/80x80/EEEEEE/999999/默认头像",
    val userName: String = "小柚同学",
    val schoolInfo: String = "未认证",
    val signature: String = "",
    // 统计数据
    val publishCount: Int = 8,
    val soldCount: Int = 3,
    val boughtCount: Int = 5,
    val collectCount: Int = 12,
    // 加载状态
    val isLoading: Boolean = false,
    val errorMsg: String = "",

    // 修改态字段
    val isEditingName: Boolean = false,
    val isEditingSignature: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val isVerifyingSchool: Boolean = false,
    // 错误提示细分
    val avatarError: String = "",
    val nameError: String = "",
    val schoolVerifyError: String = "",

    // 余额相关状态
    val balance: Float = 0.0f,          // 用户余额（默认0元）
    val isRecharging: Boolean = false,  // 充值中状态
    val rechargeError: String = "",     // 充值错误提示
    val tempRechargeAmount: String = "" // 临时输入的充值金额
)