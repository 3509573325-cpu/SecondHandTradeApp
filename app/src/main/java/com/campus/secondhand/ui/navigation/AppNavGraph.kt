package com.campus.secondhand.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.campus.secondhand.core.state.UserStateManager
import com.campus.secondhand.ui.feature.auth.AuthViewModel
import com.campus.secondhand.ui.feature.auth.LoginScreen
import com.campus.secondhand.ui.feature.auth.RegisterScreen
import com.campus.secondhand.ui.feature.home.HomeScreen
import com.campus.secondhand.ui.feature.publish.PublishScreen
import com.campus.secondhand.ui.feature.home.goodsdetail.GoodsDetailScreen
import com.campus.secondhand.ui.feature.user.UserCenterScreen
import com.campus.secondhand.ui.feature.user.myorder.MyOrderScreen
import com.campus.secondhand.ui.feature.user.schoolverify.SchoolVerifyScreen

//全局导航图（修复参数不匹配 + 回调逻辑）
@Composable
fun AppNavGraph(
    navController: NavHostController,
    navigationManager: NavigationManager,
    userStateManager: UserStateManager,
    modifier: Modifier = Modifier
) {
    // 初始化导航管理器
    navigationManager.attachNavController(navController)

    // 监听登录状态（使用collectAsStateWithLifecycle）
    val isLogin by userStateManager.isLogin.collectAsStateWithLifecycle(initialValue = false)
    val startDestination = if (isLogin) NavRoutes.HOME else NavRoutes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. 认证模块 - 登录页（匹配LoginScreen的实际参数）
        composable(NavRoutes.LOGIN) {
            // 每个页面独立获取ViewModel（避免共享状态混乱）
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) },
                onLoginSuccess = {
                    // 登录成功跳首页并清空返回栈
                    navController.navigateToHomeWithClearStack()
                },
                viewModel = authViewModel,
                modifier = Modifier
            )
        }

        // 注册页（修复参数传递：匹配RegisterScreen的实际参数）
        composable(NavRoutes.REGISTER) {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onBackToLogin = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        // 2. 主页面模块（底部导航）
        composable(NavRoutes.HOME) {
            HomeScreen(
                onGoodsClick = { goodsId ->
                    navController.navigateToGoodsDetail(goodsId)
                }
            )
        }

        composable(NavRoutes.PUBLISH) {
            PublishScreen(
                onBack = { navController.popBackStack() },
                onPublishSuccess = { navController.popBackStack() } // 补充缺失的回调
            )
        }

        composable(NavRoutes.MY_ORDER) {
            MyOrderScreen(
              //  onBack = { navController.popBackStack() },
               // onPublishSuccess = { navController.popBackStack() } // 补充缺失的回调
            )
        }

        composable(NavRoutes.USER_CENTER) {
            UserCenterScreen(
                onNavigateToSchoolVerify = {
                    //跳转到校园认证页面
                    navController.navigate(NavRoutes.SCHOOL_VERIFY)
                },
                onPublishClick = { /* 其他回调 */ },
                onOrderClick = {
                    navController.navigate(NavRoutes.MY_ORDER)
                },
                onLogoutClick = {
                    // 退出登录后导航到登录页，清空所有返回栈
                    navController.navigate(NavRoutes.LOGIN) {
                        // 清空从HOME到USER_CENTER的所有栈
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                        // 避免重复创建登录页
                        launchSingleTop = true
                        // 防止从登录页返回个人中心
                        restoreState = false
                    }
                }
            )
        }
        //校园认证页
        composable(NavRoutes.SCHOOL_VERIFY) {
            SchoolVerifyScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        //商品详情页
        composable("${NavRoutes.GOODS_DETAIL}?${NavRoutes.Params.GOODS_ID}={${NavRoutes.Params.GOODS_ID}}") { backStackEntry ->
            val goodsId = backStackEntry.arguments?.getString(NavRoutes.Params.GOODS_ID) ?: ""
            GoodsDetailScreen(
                goodsId = goodsId,
                onBackClick = { navController.popBackStack() }
            )
        }

//        composable(NavRoutes.SETTINGS) {
//            SettingsScreen(
//                onBackClick = { navController.popBackStack() },
//                onLogoutClick = {
//                    userStateManager.markAsLoggedOut()
//                    navController.navigate(NavRoutes.LOGIN) {
//                        popUpTo(NavRoutes.SETTINGS) { inclusive = true }
//                        launchSingleTop = true
//                    }
//                }
//            )
//        }
    }
}

// 导航扩展函数
fun NavHostController.navigateToHomeWithClearStack() {
    this.navigate(NavRoutes.HOME) {
        popUpTo(NavRoutes.LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavHostController.navigateToGoodsDetail(goodsId: String) {
    this.navigate("${NavRoutes.GOODS_DETAIL}?${NavRoutes.Params.GOODS_ID}=$goodsId") {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToSettings() {
    this.navigate(NavRoutes.SETTINGS) {
        launchSingleTop = true
    }
}

//@Composable
//fun SettingsScreen(onBackClick: () -> Unit, onLogoutClick: () -> Unit) {
//    androidx.compose.material3.Button(onClick = onBackClick) {
//        androidx.compose.material3.Text("返回")
//    }
//}