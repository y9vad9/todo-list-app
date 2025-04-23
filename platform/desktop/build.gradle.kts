import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // -- Compose --
    implementation(compose.runtime)
    implementation(compose.material3)
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.windowsize)

    // -- Project --
    implementation(projects.domain)
    implementation(projects.integration)
    implementation(projects.composeUi)
    implementation(projects.platform.shared)

    // -- Coroutines --
    implementation(libs.kotlinx.coroutines)

    // -- Database --
    implementation(libs.sqldelight.jvm.driver)
    implementation(libs.xerial.sqlite.jdbc)

    // -- Koin --
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    // -- Coroutines --
    implementation(libs.kotlinx.coroutines.swing)

    // -- Decompose --
    implementation(libs.decompose)
    implementation(libs.decompose.compose)
    implementation(libs.decompose.compose.experimental)
}

compose.desktop {
    application {
        mainClass = "com.y9vad9.todolist.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TodoList"
            packageVersion = "1.0.0"

            modules.add("java.sql")
            modules.add("java.desktop")
            modules.add("java.logging")

            linux {
                iconFile.set(project.file("src/main/resources/ic_launcher.png"))
            }

            macOS {
                iconFile.set(project.file("src/main/resources/ic_launcher.icns"))
            }

            windows {
                iconFile.set(project.file("src/main/resources/ic_launcher.ico"))
            }
        }

        // todo: must be fixed
        // proguard breaks the bytecode
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}


tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}