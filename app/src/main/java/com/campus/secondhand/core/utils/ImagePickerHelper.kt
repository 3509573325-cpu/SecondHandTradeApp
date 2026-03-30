package com.campus.secondhand.core.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// 1. 提前在Activity中注册Launcher
class ImagePickerHelper(activity: ComponentActivity) {
    // 提前注册，绑定Activity生命周期
    private val albumLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onImageSelected?.invoke(uri)
        }

    // 回调函数，用于接收选择的图片Uri
    private var onImageSelected: ((Uri?) -> Unit)? = null

    // 对外暴露启动选择器的方法
    fun launchAlbum(onResult: (Uri?) -> Unit) {
        onImageSelected = onResult
        albumLauncher.launch("image/*")
    }

    // 新增：将Uri转为MultipartBody.Part（供上传接口使用）
    suspend fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 将Uri转为File
                val file = uriToFile(context, uri) ?: return@withContext null
                // 2. 构建RequestBody
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                // 3. 构建MultipartPart（name="image"需和后端接口参数名一致）
                MultipartBody.Part.createFormData(
                    name = "image",
                    filename = file.name,
                    body = requestBody
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // 新增：Uri转File（私有方法）
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream ?: return null
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "upload_${System.currentTimeMillis()}.jpg"
            )
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    //在Activity中创建Helper（在onCreate中调用）
    fun ComponentActivity.createImagePickerHelper(): ImagePickerHelper {
        return ImagePickerHelper(this)
    }
    //Compose中获取Helper的便捷方法
    @Composable
    fun rememberImagePickerHelper(): ImagePickerHelper {
        val context = LocalContext.current
        return remember {
            (context as ComponentActivity).createImagePickerHelper()
        }
    }
}



