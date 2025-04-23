plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    jvm()
    jvmToolchain(11)
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()


    sourceSets {
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
    }
}

android {
    compileSdk = 36
}