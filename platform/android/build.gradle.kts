plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = 36
    namespace = "com.y9vad9.todolist.android"

    buildFeatures {
        compose = true
        viewBinding = false
    }

    defaultConfig {
        applicationId = "com.y9vad9.todolist.android"
        versionCode = 1
        versionName = "1.0.0"
        targetSdk = 36

        minSdk = 26
        compileSdk = 36
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    // -- Project --
    implementation(projects.platform.shared)
    implementation(projects.composeUi)
    implementation(projects.integration)
    implementation(projects.dependencies)

    // -- Database --
    implementation(libs.sqldelight.coroutines)
    implementation(libs.sqldelight.android.driver)

    // -- Koin --
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    // -- AndroidX --
    implementation(libs.androidx.appcompat)

    // -- Compose --
    implementation(libs.jetpack.compose.activity)
    implementation(libs.jetpack.compose.material3)
    implementation(libs.compose.windowsize)

    // -- Koin --
    implementation(libs.koin.android)
}

composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}