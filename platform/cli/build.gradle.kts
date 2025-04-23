plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    application
    java
    alias(libs.plugins.graalvm.native)
}

dependencies {
    // -- Project --
    implementation(projects.domain)
    implementation(projects.integration)
    implementation(projects.dependencies)

    // -- Database --
    implementation(libs.sqldelight.jvm.driver)
    implementation(libs.xerial.sqlite.jdbc)

    // -- CLI --
    implementation(libs.clikt.core)
    implementation(libs.mordant)
    implementation(libs.mordant.markdown)

    // -- Tests --
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    // -- File System --
    implementation(libs.squareup.okio)
}

val appName = "todolist"

application {
    applicationName = appName
    mainClass = "com.y9vad9.todolist.cli.MainKt"
}

graalvmNative {
    metadataRepository {
        enabled.set(true)
    }

    binaries {
        named("main") {
            buildArgs.addAll(
                "--initialize-at-build-time=kotlin.DeprecationLevel",
                "-H:ReflectionConfigurationFiles=${project.layout.projectDirectory.dir("src/main/resources/META-INF/native-image/reflect-config.json")}",
                "-H:ResourceConfigurationFiles=${project.layout.projectDirectory.dir("src/main/resources/META-INF/native-image/resource-config.json")}",
                "-H:Name=$appName",
            )
            //fatJar = true
        }
        named("test") {
            buildArgs.addAll(
                "--verbose",
                "-O0",
                "-H:Name=$appName",
            )
            //fatJar = true
        }
    }
}