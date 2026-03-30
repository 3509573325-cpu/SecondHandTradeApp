package com.campus.secondhand.app.di

import android.content.Context
import com.campus.secondhand.core.media.AndroidImagePicker
import com.campus.secondhand.core.media.ImagePicker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object MediaModule {

    @Provides
    @ActivityScoped
    // 使用 @ActivityContext 获取 Activity 级别的 Context
    fun provideImagePicker(@ActivityContext context: Context): ImagePicker {
        // 将 Context 强转为 ComponentActivity（安全，因为 @ActivityContext 保证是 Activity 上下文）
        val activity = context as androidx.activity.ComponentActivity
        return AndroidImagePicker(activity)
    }
}