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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campus.secondhand.R
import com.campus.secondhand.core.state.UserStateManager
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.core.utils.ValidateUtils
import com.campus.secondhand.ui.navigation.NavigationManager
import com.campus.secondhand.ui.theme.CampusSecondHandTheme

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // UI temporary state
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPwd by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var pwdError by remember { mutableStateOf("") }
    var confirmPwdError by remember { mutableStateOf("") }
    var userNameError by remember { mutableStateOf("") }

    val texts = remember {
        object {
            val registerTitle = context.getString(R.string.register_title)
            val inputUserName = context.getString(R.string.input_user_name)
            val inputPhone = context.getString(R.string.input_phone)
            val inputPassword = context.getString(R.string.input_password)
            val input_confirm_password = context.getString(R.string.input_confirm_password)
            val register = context.getString(R.string.register)
            val registerLoading = context.getString(R.string.register_loading)
            val goToLogin = context.getString(R.string.go_to_login)
            val registerSuccess = context.getString(R.string.register_success)
        }
    }
    // 监听注册成功
    LaunchedEffect(uiState) {
        if (uiState.isSuccess && !uiState.isLoading) {
            ToastUtils.show(context, texts.registerSuccess)
            onBackToLogin()
            // Reset input fields
            phone = ""
            password = ""
            confirmPwd = ""
            userName = ""
            phoneError = ""
            pwdError = ""
            confirmPwdError = ""
            userNameError = ""
        }
    }

    //监听错误信息
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            ToastUtils.show(context, uiState.errorMessage)
            viewModel.clearErrorMessage()
        }
    }

    //注册点击事件处理
    val onRegisterClick = {
        phoneError = ""
        pwdError = ""
        confirmPwdError = ""
        userNameError = ""

        var isValid = true
        if (!ValidateUtils.isPhoneValid(phone)) {
            phoneError = context.getString(R.string.phone_error_invalid)
            isValid = false
        }
        if (!ValidateUtils.isUserNameValid(userName)) {
            userNameError = context.getString(R.string.user_name_error_invalid)
            isValid = false
        }
        if (!ValidateUtils.isPwdValid(password)) {
            pwdError = context.getString(R.string.pwd_error_min_length)
            isValid = false
        }
        if (confirmPwd != password) {
            confirmPwdError = context.getString(R.string.confirm_pwd_error_mismatch)
            isValid = false
        }

        if (isValid) {
            viewModel.register(phone, password, userName)
        }
    }

    // UI布局
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = texts.registerTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text(texts.inputUserName) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = userNameError.isNotEmpty(),
                supportingText = { Text(userNameError) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(texts.inputPhone) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError.isNotEmpty(),
                supportingText = { Text(phoneError) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(texts.inputPassword) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = pwdError.isNotEmpty(),
                supportingText = { Text(pwdError) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPwd,
                onValueChange = { confirmPwd = it },
                label = { Text(texts.input_confirm_password) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPwdError.isNotEmpty(),
                supportingText = { Text(confirmPwdError) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (uiState.isLoading) texts.registerLoading else texts.register,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = texts.goToLogin,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

//@Preview(showBackground = true, name = "注册页预览", showSystemUi = true)
//@Composable
//fun RegisterScreenPreview() {
//    CampusSecondHandTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//            val mockViewModel = AuthViewModel(
//                userStateManager = UserStateManager(),
//                navigationManager = NavigationManager()
//            )
//            RegisterScreen(onBackToLogin = {}, viewModel = mockViewModel)
//        }
//    }
//}