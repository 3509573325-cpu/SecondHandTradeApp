package com.campus.secondhand.app.di

import android.content.Context
import androidx.room.Room
import com.campus.secondhand.core.constants.DbConstants
import com.campus.secondhand.core.room.AppDatabase
import com.campus.secondhand.data.local.GoodsDao
import com.campus.secondhand.data.local.OrderDao
import com.campus.secondhand.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideGoodsDao(db: AppDatabase): GoodsDao {
        return db.goodsDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao() // 从数据库实例获取 DAO
    }
}