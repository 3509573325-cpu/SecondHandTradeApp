package com.campus.secondhand.ui.navigation

import androidx.navigation.NavController
import com.campus.secondhand.ui.feature.user.myorder.MyOrderViewModel

//路由常量统一管理（按业务模块分类：认证/主页面/拓展页)
object NavRoutes {
    // 认证模块
    const val LOGIN = "login"
    const val REGISTER = "register"

    // 主页面模块（底部导航）
    const val HOME = "home"
    const val PUBLISH = "publish"
    const val USER_CENTER = "user_center"

    // 拓展页模块
    const val GOODS_DETAIL = "goods_detail"
    const val SCHOOL_VERIFY = "school_verify"
    const val SETTINGS = "settings"
    const val MY_ORDER = "my_order"

    // 路由参数名（统一管理，避免拼写错误）
    object Params {
        const val GOODS_ID = "goods_id"
    }

    // 带参数的路由拼接
    fun getGoodsDetailRoute(goodsId: String): String {
        return "$GOODS_DETAIL?${Params.GOODS_ID}=$goodsId"
    }
}

// 导航扩展函数（简化NavController调用）
//跳转到首页并清空登录页之前的栈
fun NavController.navigateToHomeWithClearStack() {
    navigate(NavRoutes.HOME) {
        popUpTo(NavRoutes.LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

//跳转到商品详情页
fun NavController.navigateToGoodsDetail(goodsId: String) {
    navigate(NavRoutes.getGoodsDetailRoute(goodsId)) {
        launchSingleTop = true
    }
}

//跳转到设置页
fun NavController.navigateToSettings() {
    navigate(NavRoutes.SETTINGS) {
        launchSingleTop = true
    }
}