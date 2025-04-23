plugins {
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
}

dependencies {
    commonMainApi(projects.domain)

    // -- FlowMVI --
    commonMainApi(libs.flowmvi.core)
    commonMainImplementation(libs.flowmvi.savedstate)
    commonMainImplementation(libs.flowmvi.essenty)

    // -- Decompose --
    commonMainApi(libs.decompose)

    // -- Time --
    commonMainImplementation(libs.kotlinx.datetime)
}

android {
    namespace = "com.y9vad9.todolist.presentation"
}