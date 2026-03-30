package com.campus.secondhand.core.media

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AndroidImagePicker(
    private val activity: ComponentActivity
) : ImagePicker {

    private val albumLauncher: ActivityResultLauncher<String> by lazy {
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                _albumImageFlow.tryEmit(uri)
            } else {
                _albumImageFlow.tryEmit(null)
            }
        }
    }

    private val _albumImageFlow = MutableSharedFlow<Uri?>(replay = 1)

    override fun pickSingleImage(): Flow<Uri?> {
        if (!activity.isFinishing && !activity.isDestroyed) {
            albumLauncher.launch("image/*")
        }
        return _albumImageFlow
    }

    // 优化压缩逻辑：返回File（方便后续转Multipart）
    override suspend fun compressImage(uri: Uri): Uri = withContext(Dispatchers.IO) {
        val file = uriToFile(activity, uri) ?: return@withContext uri
        val compressedFile = compressImageFile(file, activity)
        Uri.fromFile(compressedFile)
    }

    // 新增：重载compressImage，直接返回File
    suspend fun compressImageToFile(uri: Uri): File? = withContext(Dispatchers.IO) {
        val file = uriToFile(activity, uri) ?: return@withContext null
        compressImageFile(file, activity)
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun compressImageFile(sourceFile: File, context: Context): File {
        val compressedFile = File(context.externalCacheDir ?: context.cacheDir,
            "compressed_${System.currentTimeMillis()}.jpg")

        val options = android.graphics.BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        android.graphics.BitmapFactory.decodeFile(sourceFile.absolutePath, options)
        options.inSampleSize = calculateInSampleSize(options, 1080, 1920)
        options.inJustDecodeBounds = false

        val bitmap = android.graphics.BitmapFactory.decodeFile(sourceFile.absolutePath, options)
        val outputStream: OutputStream = FileOutputStream(compressedFile)
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)

        outputStream.flush()
        outputStream.close()
        bitmap.recycle()

        return compressedFile
    }

    private fun calculateInSampleSize(
        options: android.graphics.BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}