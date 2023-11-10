import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    id("dagger.hilt.android.plugin")
    id("com.mikepenz.aboutlibraries.plugin")
}


android {
    namespace = "rwiftkey.themes"
    compileSdk = 34

    defaultConfig {
        applicationId = "rwiftkey.themes"
        minSdk = 23
        targetSdk = 34
        versionCode = 2
        versionName = "2 TESTING"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
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
    implementation(AndroidX.compose.animation)

    debugImplementation(AndroidX.customView.poolingContainer)
    debugImplementation(AndroidX.lifecycle.viewModelCompose)
    debugImplementation(AndroidX.savedState.ktx)

    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.foundation.layout)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.ui.util)
    implementation(AndroidX.compose.runtime)
    implementation(AndroidX.compose.runtime.liveData)
    implementation(AndroidX.hilt.navigationCompose)
    implementation(AndroidX.navigation.compose)

    debugImplementation(AndroidX.compose.ui.tooling)

    implementation("com.github.topjohnwu.libsu:core:_")
    implementation("com.github.topjohnwu.libsu:io:_")
    implementation("com.github.topjohnwu.libsu:service:_")

    implementation("com.mikepenz:aboutlibraries-core:_")
    implementation("com.beust:klaxon:_")
    compileOnly("de.robv.android.xposed:api:82")

    implementation(COIL)
    implementation(COIL.gif)
    implementation(COIL.compose)

    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

    compileOnly(project(":stubs"))
}