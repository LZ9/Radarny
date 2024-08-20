plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}
//apply(from = "publish.gradle.kts")

android {
    namespace = "com.lodz.android.radarny"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        buildConfigField("int", "versionCode", "6")
        buildConfigField("String", "versionName", "\"1.0.5\"") //未上传

        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures.buildConfig = true

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.androidx.annotation)
}