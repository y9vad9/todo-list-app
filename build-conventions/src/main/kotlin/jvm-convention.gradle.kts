plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)

    sourceSets {
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
    }
}