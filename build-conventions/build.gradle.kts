plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    api(libs.kotlin.plugin)
    api(libs.android.plugin)
}