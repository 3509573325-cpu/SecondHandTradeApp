plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.campus.secondhand"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.campus.secondhand"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "com.google.dagger.hilt.android.testing.HiltTestRunnerBuilder"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/io.netty.versions.properties"
            )
        }
    }

    // ========== 版本强制规则（新增 WorkManager 约束） ==========
    configurations.all {
        resolutionStrategy {
            // 强制 DataStore 版本统一
            force ("androidx.datastore:datastore-core:$datastoreVersion")
            force ("androidx.datastore:datastore-preferences:$datastoreVersion")
            force ("androidx.datastore:datastore-preferences-core:$datastoreVersion")
            // 强制 Lifecycle 版本统一
            force ("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
            // ========== 新增：强制 WorkManager 版本（修复 PendingIntent 崩溃） ==========
            force ("androidx.work:work-runtime:2.9.0")
            force ("androidx.work:work-runtime-ktx:2.9.0")
            force ("androidx.hilt:hilt-work:$hiltNavigationVersion")
        }
    }
}

// 统一版本管理（新增 WorkManager 版本变量）
val lifecycleVersion = "2.8.4"
val datastoreVersion = "1.1.1"
val hiltVersion = "2.52"
val accompanistVersion = "0.37.3"
val composeBomVersion = "2024.09.00"
val navigationVersion = "2.8.0"
val hiltNavigationVersion = "1.2.0"
// ========== 新增：WorkManager 版本变量 ==========
val workManagerVersion = "2.9.0"

dependencies {
    // ========== 基础依赖 ==========
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(libs.androidx.appcompat)

    // ========== DataStore 依赖（已修复重复） ==========
    implementation("androidx.datastore:datastore-core:$datastoreVersion")
    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")
    implementation("androidx.datastore:datastore-preferences-core:$datastoreVersion")

    // ========== Lifecycle 依赖（已统一版本） ==========
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    // ========== 导航依赖 ==========
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltNavigationVersion")

    // ========== Hilt 依赖 ==========
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation(libs.compose.material3)
    implementation(libs.androidx.material3)
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-work:$hiltNavigationVersion")
    kapt("androidx.hilt:hilt-compiler:$hiltNavigationVersion")

    // ========== 新增：WorkManager 核心依赖（修复 PendingIntent 崩溃） ==========
    implementation("androidx.work:work-runtime-ktx:$workManagerVersion")
    implementation("androidx.work:work-gcm:$workManagerVersion") // 可选，GCM 兼容

    // ========== Room 数据库 ==========
    implementation("androidx.room:room-runtime:2.7.0")
    kapt("androidx.room:room-compiler:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")

    // ========== 网络请求 ==========
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ========== 图片加载 ==========
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ========== 协程 ==========
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // ========== Compose 扩展 ==========
    implementation("androidx.compose.material:material-icons-extended")
    //implementation("androidx.compose.material:material-icons-outlined")
    implementation("androidx.compose.foundation:foundation")

    // ========== 权限 ==========
    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")

    // ========== 图片压缩 ==========
    implementation("top.zibin:Luban:1.1.8")

    // ========== 测试依赖 ==========
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
}

// Hilt kapt 配置
kapt {
    correctErrorTypes = true
}