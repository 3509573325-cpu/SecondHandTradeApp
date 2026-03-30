package com.campus.secondhand.core.utils

import android.content.Context
import android.widget.Toast
//import androidx.compose.ui.platform.ContextAmbient
//import androidx.compose.ui.platform.LocalContext

// 全局 Toast 工具类（Compose 中获取 Context）
object ToastUtils {
    fun show(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showLong(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}