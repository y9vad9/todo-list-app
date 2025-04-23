plugins {
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // -- Project --
    commonMainImplementation(projects.presentation)

    // -- Koin --
    commonMainImplementation(libs.koin.compose)

    // -- Date and Time --
    commonMainImplementation(libs.kotlinx.datetime)

    // -- Markdown --
    commonMainImplementation(libs.compose.markdown.m3)
    commonMainImplementation(libs.compose.markdown.coil3)

    // -- Compose --
    commonMainApi(compose.ui)
    commonMainApi(libs.compose.windowsize)
    commonMainApi(compose.material3)
    commonMainApi(compose.materialIconsExtended)
    commonMainApi(compose.animation)
    commonMainApi(compose.foundation)

    // -- Decompose --
    commonMainApi(libs.decompose)
    commonMainApi(libs.decompose.compose)
    commonMainApi(libs.decompose.compose.experimental)

    // -- FlowMVI--
    commonMainImplementation(libs.flowmvi.compose)
}

android {
    namespace = "com.y9vad9.todolist.composeui"
}

kotlin {
    sourceSets {
        iosMain {
            dependencies {
                // todo: remove once https://youtrack.jetbrains.com/issue/CMP-7959/KLIB-resolver-Could-not-find-androidx.performanceperformance-annotation-for-compileKotlinIosArm64
                // is resolved
                implementation(libs.androidx.perfomance.annotation)
            }
        }
    }
}

composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}