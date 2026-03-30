package com.campus.secondhand.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.campus.secondhand.ui.navigation.bottomNavItems
import javax.inject.Inject

/**
 * 底部导航栏UI（Compose封装 + 导航逻辑绑定）
 * 企业级设计：UI与逻辑解耦，仅依赖NavController和导航模型
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    navigationManager: NavigationManager, // 注入的导航管理器
    modifier: Modifier = Modifier
) {
    // 监听当前导航栈状态
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier.height(56.dp)) {
        bottomNavItems.forEach { item ->
            AddItem(
                item = item,
                currentDestination = currentDestination,
                onClick = {
                    // 调用DI注入的导航管理器处理切换逻辑
                    navigationManager.navigateToBottomNavRoute(item.route)
                }
            )
        }
    }
}

//单个底部导航项（UI封装）
@Composable
private fun RowScope.AddItem(
    item: BottomNavItem,
    currentDestination: NavDestination?,
    onClick: () -> Unit
) {
    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

    NavigationBarItem(
        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
        label = { Text(text = item.title) },
        selected = isSelected,
        onClick = onClick,
        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}