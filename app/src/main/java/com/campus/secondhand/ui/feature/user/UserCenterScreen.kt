package com.campus.secondhand.ui.feature.user

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.campus.secondhand.core.utils.ToastUtils
import kotlinx.coroutines.flow.update


//个人中心主页面
@Composable
fun UserCenterScreen(
    viewModel: UserViewModel = hiltViewModel(),
    // 导航回调：跳转到校园认证页面
    onNavigateToSchoolVerify: () -> Unit = {},
    // 各功能入口点击事件（占位
    onPublishClick: () -> Unit = { },
    onOrderClick: () -> Unit = {  },
    onAddressClick: () -> Unit = { },
    onLogoutClick: () -> Unit = {  }
) {
    val context = LocalContext.current
    val userUiState by viewModel.userUiState.collectAsStateWithLifecycle()

    // 本地临时状态
    var showAvatarSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var tempUserName by remember { mutableStateOf(userUiState.userName) }
    var tempSignature by remember { mutableStateOf(userUiState.signature) }

    // 充值弹窗显示控制
    var showRechargeDialog by remember { mutableStateOf(false) }
    // 相册选择器
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.uploadAvatar(context, it)
            }
            showAvatarSheet = false
        }
    )

    // 初始化加载用户信息
    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
    }

    // 全局错误提示（仅消费一次）
    LaunchedEffect(userUiState.errorMsg) {
        if (userUiState.errorMsg.isNotEmpty()) {
            ToastUtils.show(context, userUiState.errorMsg)
            // 清空错误（需在ViewModel补充clearErrorMsg方法）
            viewModel.clearErrorMsg()
        }
    }

    // 细分错误提示（仅消费一次）
    LaunchedEffect(userUiState.avatarError) {
        if (userUiState.avatarError.isNotEmpty()) {
            ToastUtils.show(context, userUiState.avatarError)
            viewModel.clearError(UserViewModel.ErrorType.AVATAR)
        }
    }

    LaunchedEffect(userUiState.schoolVerifyError) {
        if (userUiState.schoolVerifyError.isNotEmpty()) {
            ToastUtils.show(context, userUiState.schoolVerifyError)
            viewModel.clearError(UserViewModel.ErrorType.SCHOOL)
        }
    }

    // 同步临时状态与UI状态
    LaunchedEffect(userUiState.userName) {
        tempUserName = userUiState.userName
    }
    LaunchedEffect(userUiState.signature) {
        tempSignature = userUiState.signature
    }

    // 核心布局
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 用户信息头部
        UserInfoHeader(
            avatarUrl = userUiState.avatarUrl,
            userName = userUiState.userName,
            schoolInfo = userUiState.schoolInfo,
            signature = userUiState.signature,
            isEditingName = userUiState.isEditingName,
            isEditingSignature = userUiState.isEditingSignature,
            isUploadingAvatar = userUiState.isUploadingAvatar,
            isLoading = userUiState.isLoading,
            tempUserName = tempUserName,
            tempSignature = tempSignature,
            onAvatarClick = { showAvatarSheet = true },
            onEditName = {
                tempUserName = userUiState.userName
                viewModel._userUiState.update { it.copy(isEditingName = true) }
            },
            onNameConfirm = {
                viewModel.updateUserProfile(it, userUiState.signature)
            },
            onNameDismiss = { viewModel._userUiState.update { it.copy(isEditingName = false) } },
            onEditSignature = {
                tempSignature = userUiState.signature
                viewModel._userUiState.update { it.copy(isEditingSignature = true) }
            },
            onSaveSignature = {
                viewModel.updateUserProfile(userUiState.userName, it)
            },
            onSignatureDismiss = { viewModel._userUiState.update { it.copy(isEditingSignature = false) } },
            onSchoolVerifyClick = onNavigateToSchoolVerify,
            onTempNameChange = { tempUserName = it },
            onTempSignatureChange = { tempSignature = it }
        )

        //余额展示区域（放在用户信息头部下方）
        BalanceSection(
            balance = userUiState.balance,
            isRecharging = userUiState.isRecharging,
            onRechargeClick = { showRechargeDialog = true }
        )

        // 头像选择Sheet
        AvatarEditSheet(
            showSheet = showAvatarSheet,
            onDismiss = { showAvatarSheet = false },
            onTakePhoto = {
                ToastUtils.show(context, "拍照功能待实现（可集成CameraX）")
                showAvatarSheet = false
            },
            onSelectFromGallery = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            isUploading = userUiState.isUploadingAvatar
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 充值弹窗
        RechargeDialog(
            showDialog = showRechargeDialog,
            tempRechargeAmount = userUiState.tempRechargeAmount,
            isRecharging = userUiState.isRecharging,
            rechargeError = userUiState.rechargeError,
            onDismiss = {
                showRechargeDialog = false
                viewModel.updateTempRechargeAmount("") // 清空输入
            },
            onAmountChange = { viewModel.updateTempRechargeAmount(it) },
            onConfirmRecharge = {
                viewModel.rechargeBalance(userUiState.tempRechargeAmount)
                showRechargeDialog = false // 充值完成后关闭弹窗
            }
        )

        LogoutConfirmDialog(
            showDialog = showLogoutDialog,
            isLoggingOut = userUiState.isLoading,
            onDismiss = { showLogoutDialog = false },
            onConfirmLogout = {
                viewModel.logout()
                showLogoutDialog = false
                // 退出成功后触发外部导航回调
                onLogoutClick()
            }
        )


        Spacer(modifier = Modifier.height(24.dp))


        // 2. 数据统计栏
        DataStatisticsBar(
            publishCount = userUiState.publishCount,
            soldCount = userUiState.soldCount,
            boughtCount = userUiState.boughtCount,
            collectCount = userUiState.collectCount
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. 功能按钮区
        FunctionButtonGrid(
            onPublishClick = onPublishClick,
            onOrderClick = onOrderClick,
            onAddressClick = onAddressClick,
            onSchoolVerifyClick = onNavigateToSchoolVerify
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. 底部信息栏
        BottomInfoBar(onLogoutClick = { showLogoutDialog = true } )
    }
}

@Composable
private fun SignatureEditSection(
    signature: String,
    tempSignature: String,
    isEditing: Boolean,
    isLoading: Boolean,
    onEditClick: () -> Unit,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    onTempSignatureChange: (String) -> Unit
) {
    Column(modifier = Modifier.clickable(enabled = !isLoading) { onEditClick() }) {
        if (isEditing) {
            // 编辑态
            OutlinedTextField(
                value = tempSignature,
                onValueChange = onTempSignatureChange,
                label = { Text("个性签名（最多50字）") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                enabled = !isLoading,
                trailingIcon = {
                    Row {
                        TextButton(
                            onClick = { onSave(tempSignature) },
                            enabled = !isLoading
                        ) {
                            Text("保存")
                        }
                        TextButton(onClick = onDismiss) {
                            Text("取消")
                        }
                    }
                }
            )
        } else {
            // 展示态
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = signature.ifBlank { "点击添加个性签名" },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑签名",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun UserNameSection(
    userName: String,
    tempUserName: String,
    isEditingName: Boolean,
    isLoading: Boolean,
    onEditClick: () -> Unit,
    onNameConfirm: (String) -> Unit,
    onNameDismiss: () -> Unit,
    onTempNameChange: (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(enabled = !isLoading) { onEditClick() }
                    .padding(4.dp)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "编辑用户名",
                modifier = Modifier
                    .size(18.dp)
                    .clickable(enabled = !isLoading) { onEditClick() },
                tint = Color.Gray
            )
        }

        // 编辑弹窗
        if (isEditingName) {
            AlertDialog(
                onDismissRequest = onNameDismiss,
                title = { Text("修改用户名") },
                text = {
                    OutlinedTextField(
                        value = tempUserName,
                        onValueChange = onTempNameChange,
                        label = { Text("请输入用户名（1-10字）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { onNameConfirm(tempUserName) },
                        enabled = !isLoading
                    ) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onNameDismiss) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvatarEditSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    isUploading: Boolean
) {
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "更换头像",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 拍照选项
                ListItem(
                    leadingContent = { Icon(Icons.Default.Camera, contentDescription = "拍照") },
                    headlineContent = { Text("拍照") },
                    modifier = Modifier
                        .clickable(enabled = !isUploading) { onTakePhoto() }
                        .padding(vertical = 8.dp)
                )

                // 从相册选择
                ListItem(
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = "相册") },
                    headlineContent = { Text("从相册选择") },
                    modifier = Modifier
                        .clickable(enabled = !isUploading) { onSelectFromGallery() }
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 上传中状态
                if (isUploading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(
                        text = "头像上传中...",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

//用户信息头部
@Composable
private fun UserInfoHeader(
    avatarUrl: String,
    userName: String,
    schoolInfo: String,
    signature: String,
    isEditingName: Boolean,
    isEditingSignature: Boolean,
    isUploadingAvatar: Boolean,
    isLoading: Boolean,
    tempUserName: String,
    tempSignature: String,
    onAvatarClick: () -> Unit,
    onEditName: () -> Unit,
    onNameConfirm: (String) -> Unit,
    onNameDismiss: () -> Unit,
    onEditSignature: () -> Unit,
    onSaveSignature: (String) -> Unit,
    onSignatureDismiss: () -> Unit,
    onSchoolVerifyClick: () -> Unit,
    onTempNameChange: (String) -> Unit,
    onTempSignatureChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 加载中遮罩
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .alpha(0.5f),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(modifier = Modifier.width(120.dp))
            }
        }

        // 重构：头像+用户名 + 右上角功能图标 一行布局
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // 两端对齐
        ) {
            // 左侧：头像 + 用户名
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(if (isLoading) 0.7f else 1f)
            ) {
                // 头像（点击触发修改）
                Image(
                    painter = rememberAsyncImagePainter(model = avatarUrl),
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isUploadingAvatar && !isLoading) { onAvatarClick() }
                        .alpha(if (isUploadingAvatar) 0.7f else 1f),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 用户名编辑区域
                UserNameSection(
                    userName = userName,
                    tempUserName = tempUserName,
                    isEditingName = isEditingName,
                    isLoading = isLoading,
                    onEditClick = onEditName,
                    onNameConfirm = onNameConfirm,
                    onNameDismiss = onNameDismiss,
                    onTempNameChange = onTempNameChange
                )
            }

            // 右侧：功能图标（消息提醒、设置、帮助）- 移到此处
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "消息提醒",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(enabled = !isLoading) { },
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(enabled = !isLoading) {  },
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Outlined.QuestionMark,
                    contentDescription = "帮助",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(enabled = !isLoading) { },
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 校园认证（点击跳转到认证页面）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = !isLoading,
                    onClick = onSchoolVerifyClick
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = schoolInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    schoolInfo.contains("未认证") -> Color.Red
                    schoolInfo.contains("审核中") -> Color(0xFFFF8C00)
                    else -> Color.Gray
                }
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "去认证",
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 个性签名编辑区域
        SignatureEditSection(
            signature = signature,
            tempSignature = tempSignature,
            isEditing = isEditingSignature,
            isLoading = isLoading,
            onEditClick = onEditSignature,
            onSave = onSaveSignature,
            onDismiss = onSignatureDismiss,
            onTempSignatureChange = onTempSignatureChange
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun BalanceSection(
    balance: Float,
    isRecharging: Boolean,
    onRechargeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "我的余额",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥ ${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            TextButton(
                onClick = onRechargeClick,
                enabled = !isRecharging,

            ) {
                Text("充值")
            }
        }
    }
}

// 充值弹窗
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RechargeDialog(
    showDialog: Boolean,
    tempRechargeAmount: String,
    isRecharging: Boolean,
    rechargeError: String,
    onDismiss: () -> Unit,
    onAmountChange: (String) -> Unit,
    onConfirmRecharge: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("余额充值") },
            text = {
                Column {
                    // 金额输入框
                    OutlinedTextField(
                        value = tempRechargeAmount,
                        onValueChange = onAmountChange,
                        label = { Text("请输入充值金额（元）") },
                        placeholder = { Text("例如：10.00") },
                        singleLine = true,
                        enabled = !isRecharging,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword // 数字键盘
                        ),
                        isError = rechargeError.isNotEmpty()
                    )
                    // 错误提示
                    if (rechargeError.isNotEmpty()) {
                        Text(
                            text = rechargeError,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    // 快捷金额选项（可选）
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("10", "20", "50", "100").forEach { amount ->
                            TextButton(
                                onClick = { onAmountChange(amount) },
                                enabled = !isRecharging
                            ) {
                                Text("¥$amount")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmRecharge,
                    enabled = !isRecharging
                ) {
                    if (isRecharging) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("确认充值")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isRecharging
                ) {
                    Text("取消")
                }
            }
        )
    }
}

//数据统计栏
@Composable
private fun DataStatisticsBar(
    publishCount: Int,
    soldCount: Int,
    boughtCount: Int,
    collectCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatisticItem(title = "我发布的", count = publishCount)
        StatisticItem(title = "我卖出的", count = soldCount)
        StatisticItem(title = "我买到的", count = boughtCount)
        StatisticItem(title = "收藏夹", count = collectCount)
    }
}

//单个统计项
@Composable
private fun StatisticItem(title: String, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

//功能按钮网格
@Composable
private fun FunctionButtonGrid(
    onPublishClick: () -> Unit,
    onOrderClick: () -> Unit,
    onAddressClick: () -> Unit,
    onSchoolVerifyClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 第一行功能按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionButton(text = "我的订单", onClick = onOrderClick)
            FunctionButton(text = "物流查询", onClick = {  })
            FunctionButton(text = "退款/售后", onClick = {  })
            FunctionButton(text = "发布闲置", onClick = onPublishClick)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 第二行功能按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionButton(text = "校园认证", onClick = { onSchoolVerifyClick() })
            FunctionButton(text = "二手集市", onClick = {  })
            FunctionButton(text = "校内求购", onClick = { })
            FunctionButton(text = "拼单组队", onClick = { })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 第三行功能按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FunctionButton(text = "收货地址", onClick = onAddressClick)
            FunctionButton(text = "支付管理", onClick = { })
            FunctionButton(text = "隐私设置", onClick = {})
            FunctionButton(text = "意见反馈", onClick = {  })
        }
    }
}
//单个功能按钮
@Composable
private fun FunctionButton(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(72.dp)
            .height(72.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

//退出登录
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogoutConfirmDialog(
    showDialog: Boolean,
    isLoggingOut: Boolean, // 退出中加载状态
    onDismiss: () -> Unit, // 取消/关闭弹窗
    onConfirmLogout: () -> Unit // 确认退出
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = { Text("您是否要退出？") },
            confirmButton = {
                TextButton(
                    onClick = onConfirmLogout,
                    enabled = !isLoggingOut // 加载中禁用按钮
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("退出", color = Color.Red)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoggingOut
                ) {
                    Text("取消")
                }
            }
        )
    }
}

//底部信息栏
@Composable
private fun BottomInfoBar(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 版本 + 客服 + 退出登录
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "v1.0.0 | 客服：400-xxxx-xxxx",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "退出登录",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.clickable { onLogoutClick() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 分割线
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.LightGray.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 隐私政策 + 用户协议
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "隐私政策",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.clickable {  }
            )
            Text(
                text = " | ",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "用户协议",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.clickable {  }
            )
        }
    }
}
// 预览
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun UserCenterScreenPreview() {
//    // 模拟ViewModel
//    val mockViewModel = object : UserViewModel() {
//        override val userUiState = androidx.lifecycle.MutableStateFlow(UserUiState())
//        override fun loadUserInfo() {}
//        override fun toggleNameEdit(editing: Boolean) {}
//        override fun updateUserName(newName: String) {}
//        override fun toggleSignatureEdit(editing: Boolean) {}
//        override fun updateSignature(newSignature: String) {}
//        override fun clearError(type: ErrorType) {}
//        override fun clearErrorMsg() {}
//    }
//
//    UserCenterScreen(viewModel = mockViewModel)
//}