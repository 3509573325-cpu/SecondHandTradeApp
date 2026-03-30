// com.campus.secondhand.app.di.NetworkModule.kt
package com.campus.secondhand.app.di

import com.campus.secondhand.core.constants.ApiConstants
import com.campus.secondhand.core.network.ApiService
import com.campus.secondhand.core.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 提供Retrofit实例
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return RetrofitClient.getRetrofit(ApiConstants.BASE_URL)
    }

    // 提供ApiService实例（Hilt自动注入）
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}