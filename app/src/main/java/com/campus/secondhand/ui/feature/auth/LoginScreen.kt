package com.campus.secondhand.ui.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campus.secondhand.R
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.core.utils.ValidateUtils

//纯UI组件：仅负责渲染和透出用户操作，无业务逻辑、无状态持有
@Composable
fun LoginScreen(
    // 跳转到注册页回调
    onNavigateToRegister: () -> Unit,
    // 登录成功回调
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 仅保留UI临时状态（输入框）
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var pwdError by remember { mutableStateOf("") }

    val texts = remember {
        object {
            val appTitle = context.getString(R.string.app_title)
            val inputPhone = context.getString(R.string.input_phone)
            val inputPassword = context.getString(R.string.input_password)
            val login = context.getString(R.string.login)
            val loginLoading = context.getString(R.string.login_loading)
            val goToRegister = context.getString(R.string.go_to_register)
            val loginSuccess = context.getString(R.string.login_success)
        }
    }
    // 监听登录成功
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            ToastUtils.show(context, texts.loginSuccess)
            onLoginSuccess()
        }
    }

    // 监听错误信息
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            ToastUtils.show(context, uiState.errorMessage)
            viewModel.clearErrorMessage()
        }
    }

    // 纯UI层验证逻辑
    fun validateAndTriggerLogin() {
        phoneError = ""
        pwdError = ""

        var isValidate = true
        if (!ValidateUtils.isPhoneValid(phone)) {
            phoneError = context.getString(R.string.phone_error_invalid)
            isValidate = false
        }
        if (!ValidateUtils.isPwdValid(password)) {
            pwdError = context.getString(R.string.pwd_error_min_length)
            isValidate = false
        }

        if (isValidate) {
            viewModel.login(phone, password) // 调用ViewModel处理登录
        }
    }

    // UI布局
    Box(
        modifier = modifier
            .padding(horizontal = 30.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = texts.appTitle,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(texts.inputPhone) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = phoneError.isNotEmpty(),
                supportingText = { if (phoneError.isNotEmpty()) Text(phoneError) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(texts.inputPassword) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = pwdError.isNotEmpty(),
                supportingText = { if (pwdError.isNotEmpty()) Text(pwdError) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = ::validateAndTriggerLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (uiState.isLoading) texts.loginLoading else texts.login,
                    modifier = Modifier.padding(8.dp)
                )
            }

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = texts.goToRegister,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 预览函数
//@Preview(showBackground = true, name = "登录页预览")
//@Composable
//fun LoginScreenPreview() {
//    CampusSecondHandTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//            // 预览时使用空实现的ViewModel（实际项目用hiltViewModel）
//            val mockViewModel = AuthViewModel(
//                userStateManager = UserStateManager(),
//                navigationManager = NavigationManager()
//            )
//            LoginScreen(
//                onNavigateToRegister = {},
//                onLoginSuccess = {},
//                viewModel = mockViewModel
//            )
//        }
//    }
//}