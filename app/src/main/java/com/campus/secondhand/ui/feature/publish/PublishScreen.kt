package com.campus.secondhand.ui.feature.publish

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.campus.secondhand.MainActivity
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.data.model.ui.GoodsCategory
import com.campus.secondhand.data.model.ui.GoodsShipType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PublishScreen(
    onBack: () -> Unit,
    onPublishSuccess: () -> Unit
) {
    val context = LocalContext.current
    val publishViewModel: PublishViewModel = hiltViewModel()

    // ========== 1. 状态变量 ==========
    // 基础信息状态
    var goodsName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("0.00") }
    var selectedImgUri by remember { mutableStateOf<String?>(null) } // 本地选择的图片Uri
    var goodsNameError by remember { mutableStateOf("") }

    // 分类/发货方式/位置状态
    var selectedCategory by remember { mutableStateOf(GoodsCategory.ALL) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedShipType by remember { mutableStateOf(GoodsShipType.FREE_SHIP) }
    var showShipTypeDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("弦高西湖凶文旅街区") }
    var showLocationDialog by remember { mutableStateOf(false) }
    var inputLocation by remember { mutableStateOf(selectedLocation) }

    // 收集ViewModel状态
    val isLoading by publishViewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val publishResult by publishViewModel.publishResult.collectAsStateWithLifecycle()
    val imageUploading by publishViewModel.imageUploading.collectAsStateWithLifecycle(initialValue = false)
    val uploadedImageUrl by publishViewModel.uploadedImageUrl.collectAsStateWithLifecycle(initialValue = "")

    // 图片选择Helper
    val activity = context as MainActivity
    val imagePickerHelper = activity.imagePickerHelper

    // ========== 2. 监听发布结果 ==========
    LaunchedEffect(publishResult) {
        publishResult?.let { result ->
            if (result.code == 200) {
                onPublishSuccess()
                // 发布成功后重置状态
                publishViewModel.resetPublishResult()
                publishViewModel.resetUploadState()
                goodsName = ""
                description = ""
                priceText = "0.00"
                selectedImgUri = null
                selectedCategory = GoodsCategory.ALL
            }
        }
    }

    // ========== 3. 选择图片 + 上传逻辑 ==========
    // 选择图片
    val onSelectImageClick = {
        imagePickerHelper.launchAlbum { uri: Uri? ->
            selectedImgUri = uri?.toString()
            // 选择图片后自动上传（也可改为手动点击上传）
            uri?.toString()?.let { imgUri ->
                publishViewModel.uploadGoodsImage(context, imgUri, imagePickerHelper)
            }
        }
    }

    // ========== 4. 发布按钮点击事件（改造：不再传imgUrl，使用ViewModel中的uploadedImageUrl） ==========
    val onPublishClick = {
        goodsNameError = ""
        var isValid = true

        // 校验逻辑
        if (goodsName.isBlank()) {
            goodsNameError = "商品名称不能为空"
            isValid = false
        }
        if (uploadedImageUrl.isBlank()) { // 校验是否上传成功
            ToastUtils.show(context, "请等待图片上传完成")
            isValid = false
        }
        if (selectedCategory == GoodsCategory.ALL) {
            ToastUtils.show(context, "请选择商品分类")
            isValid = false
        }
        if (selectedLocation.isBlank()) {
            ToastUtils.show(context, "请填写所在位置")
            isValid = false
        }

        if (isValid) {
            publishViewModel.publishGoods(
                context = context,
                goodsName = goodsName,
                price = priceText.toFloatOrNull() ?: 0f,
                description = description,
                category = selectedCategory.categoryName,
                shipType = selectedShipType.typeName,
                location = selectedLocation
            )
        }
    }

    // ========== 5. 页面布局（仅改造图片区域，添加上传中状态） ==========
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("发闲置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    Button(
                        onClick = onPublishClick,
                        enabled = !isLoading && !imageUploading, // 上传/加载中禁用发布
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        if (isLoading || imageUploading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text("发布", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 50.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            // 1. 首图上传区域（改造：添加上传中状态）
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable(enabled = !imageUploading) { onSelectImageClick() }, // 上传中禁用选择
                shape = MaterialTheme.shapes.medium
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // 图片预览
                    if (selectedImgUri.isNullOrBlank()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "添加首图",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = "+添加优质\n首图更吸引人~",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImgUri),
                            contentDescription = "商品首图",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // 上传中遮罩
                    if (imageUploading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }

            // 保留原有其他布局（商品名称/描述/价格/发货方式/位置/分类）...
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = goodsName,
                onValueChange = { goodsName = it },
                placeholder = { Text("输入商品名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = goodsNameError.isNotEmpty(),
                supportingText = { Text(goodsNameError) },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("描述一下宝贝的品牌型号、货品来源...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { /* AI帮写逻辑 */ },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = "AI帮你写",
                    color = Color(0xFF0088FF),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("价格", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = {
                                if (it.matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
                                    priceText = it
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            singleLine = true,
                            label = { Text("¥") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )
                        IconButton(onClick = { /* 价格选择器 */ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "选择价格", tint = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(1.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .clickable { showShipTypeDialog = true },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("发货方式", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedShipType.typeName, style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { showShipTypeDialog = true }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "选择发货方式", tint = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(1.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .clickable {
                        inputLocation = selectedLocation
                        showLocationDialog = true
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("所在位置", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedLocation, style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = {
                            inputLocation = selectedLocation
                            showLocationDialog = true
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "选择位置", tint = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(1.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .clickable { showCategoryDialog = true },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("商品分类", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedCategory.categoryName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(onClick = { showCategoryDialog = true }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "选择分类", tint = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // 保留原有弹窗（发货方式/位置/分类）...
    if (showShipTypeDialog) {
        AlertDialog(
            onDismissRequest = { showShipTypeDialog = false },
            title = { Text("选择发货方式") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GoodsShipType.entries.forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedShipType = type
                                    showShipTypeDialog = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = type.typeName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShipTypeDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("修改所在位置") },
            text = {
                OutlinedTextField(
                    value = inputLocation,
                    onValueChange = { inputLocation = it },
                    placeholder = { Text("输入详细位置") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedLocation = inputLocation
                    showLocationDialog = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("选择商品分类") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GoodsCategory.entries.filter { it != GoodsCategory.ALL }
                        .forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategory = category
                                        showCategoryDialog = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.categoryName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PublishScreenPreview() {
    PublishScreen(
        onBack = {},
        onPublishSuccess = {}
    )
}