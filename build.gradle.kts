buildscript {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath(Android.tools.build.gradlePlugin)
        classpath(Kotlin.gradlePlugin)
        classpath(Google.dagger.hilt.android.gradlePlugin)
    }
}