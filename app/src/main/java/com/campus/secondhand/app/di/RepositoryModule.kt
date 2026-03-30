package com.campus.secondhand.app.di

import com.campus.secondhand.core.network.ApiService
import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // 提供UserRepository
    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        userLocalDataSource: UserLocalDataSource
    ): UserRepository {
        return UserRepository(apiService, userLocalDataSource)
    }
}