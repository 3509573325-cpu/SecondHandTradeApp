package com.campus.secondhand.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.content.Context
import com.campus.secondhand.core.room.AppDatabase
import com.campus.secondhand.core.state.UserStateManager
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class CampusSecondHandApplication : Application() {
    companion object {
        // 全局ApplicationContext
        lateinit var CONTEXT: Context
            private set // 私有化set，避免外部修改
//        @Inject
//        lateinit var userStateManager: UserStateManager

    }

    override fun onCreate() {
        super.onCreate()
//        CoroutineScope(Dispatchers.IO).launch {
//            userStateManager.init()
//        }
        CONTEXT = applicationContext
        // 初始化数据库（传入ApplicationContext）
       // AppDatabase.init(CONTEXT) // 调用AppDatabase的init方法，初始化全局Context
        AppDatabase.getInstance(CONTEXT) // 预初始化数据库
    }
}