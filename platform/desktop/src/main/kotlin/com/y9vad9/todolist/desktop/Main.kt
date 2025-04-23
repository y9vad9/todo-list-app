package com.y9vad9.todolist.desktop

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.y9vad9.todolist.common.TodoComposeApp
import com.y9vad9.todolist.dependencies.initDependencies
import com.y9vad9.todolist.integration.FileSystemSettingsRepository
import kotlinx.datetime.Clock
import okio.Path.Companion.toOkioPath
import org.koin.core.KoinApplication
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.util.Locale
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.pathString

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
suspend fun main() {
    val driver = JdbcSqliteDriver(
        "jdbc:sqlite:${AppDirectory.pathString}/data.db",
    )
    val timeZoneRepository = SystemTimeZoneRepository()

    val settingsProvider =
        FileSystemSettingsRepository(path = AppDirectory.resolve("settings.json").toOkioPath())

    val koinApp: KoinApplication = initDependencies(
        sqlDriver = driver,
        timeZoneRepository = timeZoneRepository,
        clock = Clock.System,
        settingsRepository = settingsProvider,
    )

    val lifecycle = LifecycleRegistry()
    val stateKeeper = StateKeeperDispatcher()
    val rootComponentContext = DefaultComponentContext(
        lifecycle = lifecycle,
        stateKeeper = stateKeeper,
    )

    if (isMacOs) {
        System.setProperty("apple.awt.application.appearance", "system")
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "TodoList"
        ) {
            TodoComposeApp(
                koin = koinApp.koin,
                rootComponentContext = rootComponentContext,
                windowSizeClass = calculateWindowSizeClass()
            )
        }
    }
}

/**
 * Lazily initializes the path to the application directory based on the operating system.
 *
 * If folder does not exist yet, it will be created with restricted access on Unix-systems.
 *
 * @return The [Path] to the application directory specific to the current operating system.
 * @throws UnsupportedOperationException if the operating system is not supported.
 */
private val AppDirectory: Path by lazy {
    val os = System.getProperty("os.name").lowercase(Locale.ENGLISH)

    when {
        os.contains("win") -> Path(System.getenv("APPDATA") + "y9vad9.todolist")
        os.contains("mac") -> Path(
            System.getProperty("user.home"),
            "Library/Application Support/y9vad9.todolist"
        )

        os.contains("nix") || os.contains("nux") || os.contains("aix") -> Path(System.getProperty("user.home") + ".y9vad9.todolist")
        else -> throw UnsupportedOperationException("Unsupported operating system")
    }.also { path ->
        if (path.exists()) return@also

        val permissionAttrs = PosixFilePermissions.asFileAttribute(
            setOf(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE,
            )
        )

        path.createDirectory(permissionAttrs)
    }
}

private val isMacOs: Boolean =
    System.getProperty("os.name").lowercase(Locale.ENGLISH).contains("mac")