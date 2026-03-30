package com.campus.secondhand.core.network

import com.campus.secondhand.core.constants.ApiConstants
import com.campus.secondhand.core.network.ApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//Retrofit客户端工具类（支持Hilt注入）
object RetrofitClient {
    // 懒加载OkHttpClient（复用+统一配置）
    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // 开发环境打印完整日志，生产环境可改为NONE
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            // 超时配置（从常量类读取，统一管理）
            .connectTimeout(ApiConstants.TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(ApiConstants.TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(ApiConstants.TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }

    // 懒加载Gson（宽松模式，适配后端JSON）
    private val gson by lazy {
        GsonBuilder()
            .setLenient() //兼容后端返回的JSON格式小问题
            .create()
    }

    //对外提供Retrofit实例（供Hilt注入使用）
    fun getRetrofit(baseUrl: String = ApiConstants.BASE_URL): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)) // 保留Gson宽松模式
            .build()
    }

    //快捷获取ApiService
    val apiService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }
}