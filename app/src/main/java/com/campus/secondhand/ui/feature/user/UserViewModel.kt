package com.campus.secondhand.ui.feature.user

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.campus.secondhand.core.base.BaseViewModel
import com.campus.secondhand.core.utils.ImagePickerHelper
import com.campus.secondhand.data.model.remote.SchoolVerifyRequest
import com.campus.secondhand.data.model.remote.UpdateUserProfileRequest
import com.campus.secondhand.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

// 标记为Hilt ViewModel，自动注入依赖
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {
    // UI状态（仅存储临时状态：加载中、错误提示、编辑状态等）
    val _userUiState = MutableStateFlow(UserUiState())
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    init {
        // 初始化：监听Room本地用户数据变化，自动更新UI
        observeLocalUserInfo()
        observeUserBalance()
    }

    // 核心：监听Room本地缓存，UI优先展示本地数据
    private fun observeLocalUserInfo() {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collectLatest { localUser ->
                localUser?.let { user ->
                    // 本地有数据：直接更新UI（优先展示缓存）
                    _userUiState.update {
                        it.copy(
                            isLoading = false,
                            avatarUrl = user.avatarUrl,
                            userName = user.userName,
                            schoolInfo = user.schoolInfo,
                            signature = user.signature,
                            publishCount = user.publishCount,
                            soldCount = user.soldCount,
                            boughtCount = user.boughtCount,
                            collectCount = user.collectCount,
                            //verifyStatus = user.verifyStatus,
                            errorMsg = "" // 清空之前的错误
                        )
                    }
                } ?: run {
                    // 本地无数据：标记未登录/空状态
                    _userUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "用户未登录，请先登录",
                            avatarUrl = "",
                            userName = "xxx",
                            schoolInfo = "xxx",
                            signature = "xxxx"
                        )
                    }
                }
            }
        }
    }

    private fun observeUserBalance() {
        viewModelScope.launch {
            userRepository.observeUserBalance().collectLatest { balance ->
                _userUiState.update {
                    it.copy(
                        balance = balance?.toFloat() ?: 0.0f // 转换为Float适配UI（或直接用BigDecimal）
                    )
                }
            }
        }
    }
    // 加载用户信息：仅作为「后台同步网络数据到Room」的触发
    // UI不依赖此接口返回，而是通过observeLocalUserInfo自动更新
    fun loadUserInfo() {
        viewModelScope.launch {
            _userUiState.update { it.copy(isLoading = true) }
            try {
                // 调用网络接口（仅为了同步到Room）
                userRepository.getUserInfo()
                _userUiState.update { it.copy(isLoading = false, errorMsg = "") }
            } catch (e: Exception) {
                // 网络同步失败：仅提示，不影响本地UI展示
                _userUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = "网络同步失败，展示本地缓存：${e.message}"
                    )
                }
            }
        }
    }

    // 更新用户名+个性签名：先请求网络→同步到Room→UI自动更新
    fun updateUserProfile(newName: String, newSignature: String) {
        viewModelScope.launch {
            // 前端校验
            if (newName.isBlank() || newName.length > 10) {
                _userUiState.update { it.copy(nameError = "用户名不能为空且不超过10字") }
                return@launch
            }
            if (newSignature.length > 50) {
                _userUiState.update { it.copy(nameError = "个性签名不超过50字") }
                return@launch
            }

            _userUiState.update { it.copy(isLoading = true, nameError = "") }
            try {
                val request = UpdateUserProfileRequest(
                    userName = newName,
                    signature = newSignature
                )
                // 调用网络接口（成功后自动同步到Room）
                val response = userRepository.updateUserProfile(request)
                if (response.code != 200) {
                    // 网络更新失败：提示，但本地缓存不变
                    _userUiState.update {
                        it.copy(
                            isLoading = false,
                            nameError = response.msg
                        )
                    }
                } else {
                    // 网络更新成功：Room已同步，UI会通过observeLocalUserInfo自动刷新
                    _userUiState.update {
                        it.copy(
                            isLoading = false,
                            isEditingName = false,
                            isEditingSignature = false
                        )
                    }
                }
            } catch (e: Exception) {
                _userUiState.update {
                    it.copy(
                        isLoading = false,
                        nameError = "网络异常，本地缓存未变更：${e.message}"
                    )
                }
            }
        }
    }

    // 上传头像：网络上传→同步到Room→UI自动更新
    fun uploadAvatar(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _userUiState.update { it.copy(isUploadingAvatar = true) }
            try {
                val imagePickerHelper = ImagePickerHelper(context as ComponentActivity)
                val imagePart = imagePickerHelper.uriToMultipart(context, imageUri)
                    ?: throw Exception("图片转换失败")

                // 网络上传（成功后同步到Room）
                val response = userRepository.uploadAvatar(imagePart)
                if (response.code == 200) {
                    // 上传成功：Room已同步，UI自动刷新
                    _userUiState.update {
                        it.copy(
                            isUploadingAvatar = false,
                            avatarError = ""
                        )
                    }
                } else {
                    // 上传失败：提示，本地头像不变
                    _userUiState.update {
                        it.copy(
                            isUploadingAvatar = false,
                            avatarError = response.msg
                        )
                    }
                }
            } catch (e: Exception) {
                _userUiState.update {
                    it.copy(
                        isUploadingAvatar = false,
                        avatarError = "上传失败：${e.message}"
                    )
                }
            }
        }
    }

    // 提交校园认证：网络提交→同步到Room→UI自动更新
    fun submitSchoolVerify(school: String, grade: String, studentId: String) {
        viewModelScope.launch {
            // 前端校验
            if (school.isBlank() || grade.isBlank() || studentId.isBlank()) {
                _userUiState.update { it.copy(schoolVerifyError = "请填写完整认证信息") }
                return@launch
            }

            _userUiState.update { it.copy(isVerifyingSchool = true, schoolVerifyError = "") }
            try {
                val request = SchoolVerifyRequest(
                    school = school,
                    grade = grade,
                    studentId = studentId
                )
                // 网络提交（成功后同步到Room）
                val response = userRepository.submitSchoolVerify(request)
                if (response.code == 200) {
                    // 提交成功：Room已同步，UI自动刷新
                    _userUiState.update {
                        it.copy(
                            isVerifyingSchool = false
                        )
                    }
                } else {
                    // 提交失败：提示，本地缓存不变
                    _userUiState.update {
                        it.copy(
                            isVerifyingSchool = false,
                            schoolVerifyError = response.msg
                        )
                    }
                }
            } catch (e: Exception) {
                _userUiState.update {
                    it.copy(
                        isVerifyingSchool = false,
                        schoolVerifyError = "网络异常：${e.message}"
                    )
                }
            }
        }
    }
    // 2. 新增充值相关函数
    fun updateTempRechargeAmount(amount: String) {
        _userUiState.update { it.copy(tempRechargeAmount = amount) }
    }

    fun rechargeBalance(amount: String) {
        viewModelScope.launch {
            // 校验
            val userId = try {
                userRepository.getCurrentLoginUserId()
            } catch (e: IllegalStateException) {
                _userUiState.update {
                    it.copy(
                        isRecharging = false,
                        rechargeError = e.message ?: "用户未登录，无法充值"
                    )
                }
                return@launch
            }
            val rechargeAmount = try {
                amount.toBigDecimal()
            } catch (_: NumberFormatException) {
                _userUiState.update { it.copy(rechargeError = "请输入有效的金额") }
                return@launch
            }
            if (rechargeAmount <= BigDecimal.ZERO || rechargeAmount > BigDecimal("10000")) {
                _userUiState.update { it.copy(rechargeError = "充值金额需大于0且不超过10000元") }
                return@launch
            }

            _userUiState.update { it.copy(isRecharging = true, rechargeError = "") }
            try {
                // 调用Repository持久化方法（替换原有模拟延迟）
                val isSuccess = userRepository.rechargeUserBalance(userId, rechargeAmount)
                if (isSuccess) {
                    // 成功：余额已更新，UI会通过observeUserBalance自动刷新
                    _userUiState.update {
                        it.copy(
                            isRecharging = false,
                            tempRechargeAmount = "" // 清空输入
                        )
                    }
                } else {
                    _userUiState.update {
                        it.copy(
                            isRecharging = false,
                            rechargeError = "充值失败，余额未变更"
                        )
                    }
                }
            } catch (e: Exception) {
                _userUiState.update {
                    it.copy(
                        isRecharging = false,
                        rechargeError = "充值失败：${e.message}"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _userUiState.update { it.copy(isLoading = true, errorMsg = "") }
            try {
                // 清除本地登录状态
                userRepository.logout()
                // 清空本地用户数据（Room）
                // 更新UI状态
                _userUiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                userRepository.logout()
                // 提示错误
                _userUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = "退出失败：${e.message}"
                    )
                }
            }
        }
    }

    // 清空错误提示
    fun clearError(type: ErrorType) {
        _userUiState.update {
            when (type) {
                ErrorType.AVATAR -> it.copy(avatarError = "")
                ErrorType.NAME -> it.copy(nameError = "")
                ErrorType.SCHOOL -> it.copy(schoolVerifyError = "")
                ErrorType.RECHARGE -> it.copy(rechargeError = "")
            }
        }
    }

    // 编辑状态切换（仅UI临时状态）
    fun toggleNameEditing(editing: Boolean) {
        _userUiState.update { it.copy(isEditingName = editing) }
    }

    fun toggleSignatureEditing(editing: Boolean) {
        _userUiState.update { it.copy(isEditingSignature = editing) }
    }
    override fun clearErrorMsg() {
        _userUiState.update { it.copy(errorMsg = "") }
    }
    enum class ErrorType { AVATAR, NAME, SCHOOL, RECHARGE }
}
