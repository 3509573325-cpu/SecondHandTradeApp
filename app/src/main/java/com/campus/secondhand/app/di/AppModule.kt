package com.campus.secondhand.app.di

import com.campus.secondhand.core.state.UserStateManager
import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    // 为 UserStateManager 提供单例依赖
    @Provides
    @Singleton
    fun provideUserStateManager(
        userRepository: UserRepository, // Hilt自动注入UserRepository
        userLocalDataSource: UserLocalDataSource
    ): UserStateManager {
        return UserStateManager(userRepository, userLocalDataSource) // 补全构造参数
    }
}