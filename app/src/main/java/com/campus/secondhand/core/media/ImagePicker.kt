package com.campus.secondhand.core.media

import android.net.Uri
import kotlinx.coroutines.flow.Flow

//图片选择器核心接口
interface ImagePicker {
    /**
     * 打开系统相册选择单张图片
     * @return Flow<Uri?> 图片Uri流（协程+Flow替代回调）
     */
    fun pickSingleImage(): Flow<Uri?>

    /**
     * 压缩图片（减少上传体积）
     * @param uri 原始图片Uri
     * @return 压缩后的图片Uri
     */
    suspend fun compressImage(uri: Uri): Uri
}