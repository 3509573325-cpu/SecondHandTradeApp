package com.campus.secondhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.campus.secondhand.core.state.UserStateManager
import com.campus.secondhand.core.utils.ImagePickerHelper
import com.campus.secondhand.ui.navigation.AppNavGraph
import com.campus.secondhand.ui.navigation.BottomNavigationBar
import com.campus.secondhand.ui.navigation.NavRoutes
import com.campus.secondhand.ui.navigation.NavigationManager
import com.campus.secondhand.ui.theme.CampusSecondHandTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//应用主入口Activity（依赖注入 + 导航初始化 + 生命周期管理）
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var imagePickerHelper: ImagePickerHelper
    @Inject
    lateinit var navigationManager: NavigationManager
    @Inject
    lateinit var userStateManager: UserStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerHelper = createImagePickerHelper(this)

        setContent {
            CampusSecondHandTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // 1. 监听当前路由（关键：获取当前页面的路由）
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // 2. 定义需要显示底部导航的路由列表
                    val showBottomNavRoutes = listOf(
                        NavRoutes.HOME,
                        NavRoutes.PUBLISH,
                        NavRoutes.USER_CENTER
                    )
                    // 3. 判断：当前路由在列表中 + 已登录，才显示底部导航
                    val isLogin = userStateManager.isLogin.collectAsStateWithLifecycle(initialValue = false).value
                    val shouldShowBottomNav = isLogin && currentRoute in showBottomNavRoutes

                    Box(modifier = Modifier.fillMaxSize()) {
                        // 挂载全局导航图
                        AppNavGraph(
                            navController = navController,
                            navigationManager = navigationManager,
                            userStateManager = userStateManager
                        )

                        // 4. 仅满足条件时显示底部导航栏
                        if (shouldShowBottomNav) {
                            BottomNavigationBar(
                                navController = navController,
                                navigationManager = navigationManager,
                                modifier = Modifier.align(alignment = Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationManager.detachNavController()
    }

    fun createImagePickerHelper(activity: MainActivity): ImagePickerHelper {
        return ImagePickerHelper(activity)
    }
}