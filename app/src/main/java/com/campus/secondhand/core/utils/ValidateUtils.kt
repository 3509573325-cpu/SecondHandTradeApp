package com.campus.secondhand.core.utils

import java.util.regex.Pattern

//输入验证工具类
object ValidateUtils {
    // 手机号正则（11位数字，以1开头）
    private val PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$")
    // 密码正则（至少6位，仅字母/数字）
    private val PWD_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,}$")
    // 用户名正则（2-10位，中文/字母/数字）
    private val USER_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9]{2,10}$")

    // 验证手机号
    fun isPhoneValid(phone: String): Boolean {
        return PHONE_PATTERN.matcher(phone).matches()
    }

    // 验证密码
    fun isPwdValid(pwd: String): Boolean {
        return PWD_PATTERN.matcher(pwd).matches()
    }

    // 验证用户名
    fun isUserNameValid(userName: String): Boolean {
        return USER_NAME_PATTERN.matcher(userName).matches()
    }
}