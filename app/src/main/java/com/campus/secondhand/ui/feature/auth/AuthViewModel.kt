package com.campus.secondhand.ui.feature.auth

import com.campus.secondhand.core.base.BaseViewModel
import com.campus.secondhand.core.network.NetworkResult
import com.campus.secondhand.core.state.UserStateManager
import com.campus.secondhand.data.model.remote.LoginRequest
import com.campus.secondhand.data.model.remote.RegisterRequest
import com.campus.secondhand.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userStateManager: UserStateManager,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // 登录逻辑
    fun login(phone: String, password: String) {
        launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val request = LoginRequest(phone, password)
            try {
                val result = authRepository.login(request)
                if (result.code == 200 && result.data != null) {
                    // 登录成功：更新状态+标记登录
                    userStateManager.markAsLoggedIn()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = ""
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.msg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "登录失败：${e.message ?: "网络异常"}"
                )
            }
        }
    }

    // 注册逻辑
    fun register(phone: String, password: String, userName: String) {
        launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val request = RegisterRequest(phone, password, userName)
            try {
                when (val result = authRepository.register(request)) {
                    is NetworkResult.Success<*> -> {
                        if (result.code == 200) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = ""
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = result.msg
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "注册失败：${e.message ?: "网络异常"}"
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}