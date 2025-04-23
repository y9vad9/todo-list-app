plugins {
    alias(libs.plugins.cashapp.sqldelight)
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

sqldelight {
    databases {
        create("TodoListDatabase") {
            generateAsync.set(true)
            packageName.set("com.y9vad9.todolist.database")
        }
    }
}

dependencies {
    // -- Project --
    commonMainImplementation(projects.domain)
    commonMainImplementation(projects.presentation)

    // -- Serialization --
    commonMainApi(libs.kotlinx.serialization)

    // -- Database --
    commonMainImplementation(libs.sqldelight.coroutines)
    commonMainImplementation(libs.sqldelight.runtime)

    // -- Tests --
    jvmTestImplementation(libs.sqldelight.jvm.driver)
    jvmTestImplementation(libs.kotlin.test.junit)
    jvmTestImplementation(libs.kotlinx.coroutines.test)

    // -- File System --
    commonMainImplementation(libs.squareup.okio)
}

android {
    namespace = "com.y9vad9.todolist.integration"
}

kotlin {
    sourceSets {
        androidMain {
            dependsOn(jvmMain.get())
        }
    }
}