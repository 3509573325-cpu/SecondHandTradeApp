package com.campus.secondhand.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//ViewModel基类，封装通用协程逻辑和异常处理
open class BaseViewModel : ViewModel() {
    // 加载状态（使用StateFlow，Compose推荐）
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误信息（使用StateFlow）
    private val _errorMsg = MutableStateFlow("")
    val errorMsg: StateFlow<String> = _errorMsg.asStateFlow()

    // 全局异常处理器
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    //启动协程（带异常处理）
    protected fun launch(
        showLoading: Boolean = true,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(exceptionHandler) {
            if (showLoading) {
                _isLoading.value = true
            }
            try {
                block()
            } finally {
                if (showLoading) {
                    _isLoading.value = false
                }
            }
        }
    }

    //设置错误信息
    protected fun setErrorMsg(msg: String) {
        _errorMsg.value = msg
    }

    //清空错误信息
    open fun clearErrorMsg() {
        _errorMsg.value = ""
    }

    //异常处理（子类可重写）
    protected open fun handleException(throwable: Throwable) {
        // 统一处理异常，设置错误信息
        val errorMsg = when (throwable) {
            is java.net.ConnectException -> "网络连接失败，请检查网络"
            is java.net.SocketTimeoutException -> "请求超时，请稍后重试"
            else -> "操作失败：${throwable.message ?: "未知错误"}"
        }
        setErrorMsg(errorMsg)
        throwable.printStackTrace()
    }

    open fun onLoading(isLoading: Boolean) {}
}