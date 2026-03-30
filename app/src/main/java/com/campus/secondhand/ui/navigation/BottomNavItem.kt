package com.campus.secondhand.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

//底部导航项数据模型（组件化封装，单一职责原则）
data class BottomNavItem(
    val route: String,          // 对应路由
    val title: String,          // 显示文字
    val icon: ImageVector       // 图标
)

//底部导航列表（统一初始化，便于维护）
val bottomNavItems = listOf(
    BottomNavItem(
        route = NavRoutes.HOME,
        title = "首页",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = NavRoutes.PUBLISH,
        title = "发布商品",
        icon = Icons.Default.Add
    ),
    BottomNavItem(
        route = NavRoutes.USER_CENTER,
        title = "我的",
        icon = Icons.Default.Person
    )
)