package com.campus.secondhand.ui.navigation

import androidx.navigation.NavController
import javax.inject.Inject
import javax.inject.Singleton

//导航行为管理器（Hilt注入，解耦NavController依赖）统一封装导航逻辑，便于测试和复用
@Singleton
class NavigationManager @Inject constructor() {
    // 持有NavController实例（Compose中初始化）
    private var navController: NavController? = null

    // 初始化NavController
    fun attachNavController(navController: NavController) {
        this.navController = navController
    }

    // 清空NavController（避免内存泄漏）
    fun detachNavController() {
        this.navController = null
    }

    // 基础跳转（保留返回栈）
    fun navigate(route: String) {
        navController?.navigate(route) {
            // 避免重复入栈
            launchSingleTop = true
        }
    }

    // 跳转并清空目标路由之前的栈（如登录成功跳首页）
    fun navigateWithClearBackStack(route: String, popUpToRoute: String) {
        navController?.navigate(route) {
            popUpTo(popUpToRoute) { inclusive = true }
            launchSingleTop = true
        }
    }

    // 带参数跳转（封装参数拼接逻辑）
    fun navigateWithParams(route: String, params: Map<String, String>) {
        val routeWithParams = buildString {
            append(route)
            if (params.isNotEmpty()) {
                append("?")
                params.entries.forEachIndexed { index, (key, value) ->
                    append("$key=$value")
                    if (index != params.size - 1) append("&")
                }
            }
        }
        navigate(routeWithParams)
    }

    // 返回上一页
    fun popBackStack() {
        navController?.popBackStack()
    }

    // 底部导航切换：跳转到首页并清空当前栈（统一返回栈）
    fun navigateToBottomNavRoute(route: String) {
        navController?.navigate(route) {
            // 清空底部导航页的返回栈，切页后回首页
            popUpTo(NavRoutes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }
}