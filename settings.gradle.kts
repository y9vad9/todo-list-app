enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.y9vad9.com")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "todolist-app"

includeBuild("build-conventions")

include(
    ":domain",
    ":integration",
)

include(
    ":platform:cli",
    ":platform:desktop",
    ":platform:shared",
    ":platform:android",
)

include(":presentation")

include(":dependencies")

include(":compose-ui")