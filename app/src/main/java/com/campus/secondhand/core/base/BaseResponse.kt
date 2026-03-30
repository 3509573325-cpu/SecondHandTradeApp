package com.campus.secondhand.core.base

open class BaseResponse(
    val code: Int = -1, // 200=成功
    val msg: String = "" // 提示信息
)