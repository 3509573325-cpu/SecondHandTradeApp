package com.campus.secondhand.data.model.remote

import com.campus.secondhand.core.base.BaseResponse

// 图片上传接口返回格式
data class UploadImageResponse(
    val data: ImageUrlData? = null // 图片URL数据
): BaseResponse()

// 图片URL数据体
data class ImageUrlData(
    val imageUrl: String = "" // 后端返回的图片访问URL（如http://192.168.3.5:8080/images/xxx.jpg）
)