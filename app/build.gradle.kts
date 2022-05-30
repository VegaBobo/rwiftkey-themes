import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    id("dagger.hilt.android.plugin")
}


android {
    namespace = "com.rswiftkey"
    compileSdk = 32

    defaultConfig {
        applicationId = "com.rswiftkey"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.runtime)
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.documentFile)
    implementation(AndroidX.dataStore.preferences)

    implementation(Google.Android.Material)

    // TODO : Remove these dependency once we upgrade to Android Studio Dolphin b/228889042
    // These dependencies are currently necessary to render Compose previews
    debugImplementation("androidx.customview:customview-poolingcontainer:_")
    debugImplementation(AndroidX.lifecycle.viewModelCompose)
    debugImplementation(AndroidX.savedState.ktx)

    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.foundation.layout)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.ui.util)
    implementation(AndroidX.compose.runtime)
    implementation(AndroidX.compose.runtime.liveData)
    implementation(AndroidX.hilt.navigationCompose)
    implementation(AndroidX.navigation.compose)

    debugImplementation(AndroidX.compose.ui.tooling)

    implementation("com.github.topjohnwu.libsu:core:_")
    implementation("com.github.topjohnwu.libsu:io:_")

    implementation("com.beust:klaxon:_")

    implementation(COIL)
    implementation(COIL.gif)
    implementation(COIL.compose)

    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

}