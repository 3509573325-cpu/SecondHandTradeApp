package com.campus.secondhand.ui.feature.publish

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.campus.secondhand.core.base.BaseViewModel
import com.campus.secondhand.core.network.RetrofitClient
import com.campus.secondhand.core.room.AppDatabase
import com.campus.secondhand.core.utils.ImagePickerHelper
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.data.model.local.UserEntity
import com.campus.secondhand.data.model.remote.PublishGoodsRequest
import com.campus.secondhand.data.model.remote.PublishGoodsResponse
import com.campus.secondhand.data.repository.GoodsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class PublishViewModel @Inject constructor(
    private val goodsRepository: GoodsRepository,
) : BaseViewModel() {
    private val _publishResult = MutableStateFlow<PublishGoodsResponse?>(null)
    open val publishResult: StateFlow<PublishGoodsResponse?> = _publishResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    //图片上传相关状态
    private val _imageUploading = MutableStateFlow(false) // 图片上传中
    open val imageUploading: StateFlow<Boolean> = _imageUploading.asStateFlow()

    private val _uploadedImageUrl = MutableStateFlow("") // 上传成功后的图片URL
    open val uploadedImageUrl: StateFlow<String> = _uploadedImageUrl.asStateFlow()

    // 获取当前登录用户
    private suspend fun getCurrentUser(context: Context): UserEntity? {
        return AppDatabase.getInstance(context).userDao().getCurrentLoginUser()
    }

    // 图片上传方法
    fun uploadGoodsImage(context: Context, imageUri: String, imagePickerHelper: ImagePickerHelper) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            viewModelScope.launch(Dispatchers.Main) { // 切换到主线程
                _imageUploading.value = false
                ToastUtils.show(context, "图片上传失败：${throwable.message}")
            }
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _imageUploading.value = true
            // 1. 解析Uri
            val uri = imageUri.toUri()
            // 2. 转为MultipartPart
            val imagePart = imagePickerHelper.uriToMultipart(context, uri)
            if (imagePart == null) {
                withContext(Dispatchers.Main) { // 切换到主线程
                    _imageUploading.value = false
                    ToastUtils.show(context, "图片格式错误")
                }
                return@launch
            }
            // 3. 调用上传接口
            val response = RetrofitClient.apiService.uploadImage(imagePart)

            withContext(Dispatchers.Main) { // 切换到主线程
                _imageUploading.value = false
                if (response.code == 200) {
                    val imageUrl = response.data?.imageUrl ?: ""
                    if (imageUrl.isNotBlank()) {
                        _uploadedImageUrl.value = imageUrl
                        ToastUtils.show(context, "图片上传成功")
                    } else {
                        ToastUtils.show(context, "图片URL为空")
                    }
                } else {
                    ToastUtils.show(context, response.msg)
                }
            }
        }
    }

    // 发布商品方法
    fun publishGoods(
        context: Context,
        goodsName: String,
        price: Float,
        description: String,
        category: String,
        shipType: String = "",
        location: String = ""
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            viewModelScope.launch(Dispatchers.Main) {
                _isLoading.value = false
                ToastUtils.show(context, "发布失败：${throwable.message}")
                _publishResult.value = null
            }
        }

        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true

            // 校验登录状态
            val user = getCurrentUser(context)
            if (user == null) {
                withContext(Dispatchers.Main) {
                    ToastUtils.show(context, "请先登录")
                    _isLoading.value = false
                }
                return@launch
            }

            // 校验图片URL是否存在
            val imageUrl = _uploadedImageUrl.value
            if (imageUrl.isBlank()) {
                withContext(Dispatchers.Main) {
                    ToastUtils.show(context, "请先上传商品图片")
                    _isLoading.value = false
                }
                return@launch
            }

            // 构建发布请求
            val request = PublishGoodsRequest(
                goodsName = goodsName,
                price = price,
                description = description,
                category = category,
                sellerId = user.userId,
                sellerName = user.userName,
                shipType = shipType,
                location = location,
                imageUrl = imageUrl
            )

            // 调用接口
            val response = RetrofitClient.apiService.publishGoods(request)

            withContext(Dispatchers.Main) {
                _isLoading.value = false
                _publishResult.value = response
                if (response.code == 200) {
                    ToastUtils.show(context, "发布成功！")
                } else {
                    ToastUtils.show(context, response.msg)
                }
            }
        }
    }

    //重置图片上传状态
    fun resetUploadState() {
        _uploadedImageUrl.value = ""
    }

    //重置发布结果（页面复用/重置状态）
    fun resetPublishResult() {
        _publishResult.value = null
    }
}