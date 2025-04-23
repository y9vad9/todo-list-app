plugins {
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
    alias(libs.plugins.google.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}

dependencies {
    // -- Project --
    commonMainImplementation(projects.domain)
    commonMainImplementation(projects.integration)
    commonMainImplementation(projects.presentation)

    // -- Decompose --
    commonMainImplementation(libs.decompose)

    // -- DI --
    commonMainApi(libs.koin.core)
    commonMainImplementation(libs.koin.annotations)

    ksp(libs.koin.ksp.compiler)
}

android {
    namespace = "com.y9vad9.todolist.dependencies"
}