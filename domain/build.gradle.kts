plugins {
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
}

dependencies {
    commonMainApi(libs.y9vad9.ktiny.kotlidator)

    // -- Date and Time --
    commonMainApi(libs.kotlinx.datetime)
    commonMainImplementation(libs.kotlinx.coroutines)

    // -- Tests --
    jvmTestImplementation(libs.kotlin.test.junit)
    jvmTestImplementation(libs.kotlinx.coroutines.test)
    jvmTestImplementation(libs.mockk)
}

android {
    namespace = "com.y9vad9.todolist.domain"
}