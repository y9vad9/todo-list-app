import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    // -- Koin --
    commonMainApi(libs.koin.core)
    commonMainApi(libs.koin.compose)

    // -- Project --
    commonMainApi(projects.domain)
    commonMainApi(projects.integration)
    commonMainApi(projects.presentation)
    commonMainApi(projects.dependencies)
    commonMainApi(projects.composeUi)

    // -- Database --
    commonMainApi(libs.sqldelight.runtime)

    // -- FileSystem --
    commonMainApi(libs.squareup.okio)

    // -- Decompose & Essenty --
    commonMainApi(libs.decompose)
    commonMainApi(libs.essenty.lifecycle)
    commonMainApi(libs.essenty.stateKeeper)
}

android {
    namespace = "com.y9vad9.todolist.shared"
}

kotlin {
    applyDefaultHierarchyTemplate()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true

            export(projects.domain)
            export(projects.composeUi)
            export(projects.presentation)
            export(projects.dependencies)
            export(libs.sqldelight.native.driver)
            export(libs.decompose)

            export(libs.essenty.lifecycle)
            export(libs.essenty.stateKeeper)

            export(libs.sqldelight.native.driver)
        }
    }

    sourceSets {
        iosMain {
            dependencies {
                api(projects.composeUi)
                api(libs.sqldelight.native.driver)
            }
        }
    }
}

val iosAppDebugFrameworkDir = rootProject
    .file("platform/ios/Frameworks/Shared.framework")

val iosAppReleaseFrameworkDir = rootProject
    .file("platform/ios/Frameworks/Shared.framework")

val copyDebugFrameworkToXcode by tasks.registering(Sync::class) {
    dependsOn("linkDebugFrameworkIosFat")

    val debugFrameworkDir = layout.buildDirectory.dir("fat-framework/debug/shared.framework")

    from(debugFrameworkDir)
    into(iosAppDebugFrameworkDir)

    doFirst {
        iosAppDebugFrameworkDir.deleteRecursively()
    }
}

val copyReleaseFrameworkToXcode by tasks.registering(Sync::class) {
    dependsOn("linkReleaseFrameworkIosFat")

    val releaseFrameworkDir = layout.buildDirectory.dir("fat-framework/release/shared.framework")

    from(releaseFrameworkDir)
    into(iosAppReleaseFrameworkDir)

    doFirst {
        iosAppReleaseFrameworkDir.deleteRecursively()
    }
}

tasks.register("prepareXcodeDebug") {
    dependsOn(copyDebugFrameworkToXcode)
}

tasks.register("prepareXcodeRelease") {
    dependsOn(copyReleaseFrameworkToXcode)
}

composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}