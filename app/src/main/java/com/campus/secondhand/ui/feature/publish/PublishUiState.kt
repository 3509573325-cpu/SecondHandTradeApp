package com.campus.secondhand.ui.feature.publish

// 后续可扩展UI状态管理，当前先保留基础类
data class PublishUiState(
    val isLoading: Boolean = false,
    val publishSuccess: Boolean = false,
    val errorMsg: String = ""
)