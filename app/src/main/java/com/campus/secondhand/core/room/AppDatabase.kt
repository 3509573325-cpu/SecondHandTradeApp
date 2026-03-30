package com.campus.secondhand.core.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.campus.secondhand.data.local.GoodsDao
import com.campus.secondhand.data.local.OrderDao
import com.campus.secondhand.data.local.UserDao
import com.campus.secondhand.data.model.local.BigDecimalConverter
import com.campus.secondhand.data.model.local.GoodsEntity
import com.campus.secondhand.data.model.local.OrderEntity
import com.campus.secondhand.data.model.local.UserEntity

// exportSchema=false 避免schema校验
@Database(
    entities = [UserEntity::class, GoodsEntity::class, OrderEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(BigDecimalConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun goodsDao(): GoodsDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campus_secondhand_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}