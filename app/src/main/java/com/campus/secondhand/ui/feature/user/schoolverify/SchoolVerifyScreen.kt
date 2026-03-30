package com.campus.secondhand.ui.feature.user.schoolverify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.ui.feature.user.UserViewModel

// SchoolVerifyScreen.kt 校园认证页面
@Composable
fun SchoolVerifyScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val userUiState by viewModel.userUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 表单状态
    var school by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部返回栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "校园认证",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 认证说明
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.1f))
        ) {
            Text(
                text = "⚠️ 认证说明：\n1. 仅支持本校学生认证\n2. 认证信息将严格保密\n3. 审核时间为1-2个工作日",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 表单区域
        OutlinedTextField(
            value = school,
            onValueChange = { school = it },
            label = { Text("学校名称") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = grade,
            onValueChange = { grade = it },
            label = { Text("年级专业（例：计算机21级）") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("学号") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // 错误提示
        if (userUiState.schoolVerifyError.isNotEmpty()) {
            LaunchedEffect(userUiState.schoolVerifyError) {
                ToastUtils.show(context, userUiState.schoolVerifyError)
                viewModel.clearError(UserViewModel.ErrorType.SCHOOL)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 提交按钮
        Button(
            onClick = { viewModel.submitSchoolVerify(school, grade, studentId) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !userUiState.isVerifyingSchool
        ) {
            if (userUiState.isVerifyingSchool) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("提交认证")
            }
        }
    }
}