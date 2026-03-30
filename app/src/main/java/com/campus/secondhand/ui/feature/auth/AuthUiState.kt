package com.campus.secondhand.ui.feature.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false
)