package com.y9vad9.todolist.cli

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.core.subcommands
import com.y9vad9.todolist.cli.commands.CompleteCommand
import com.y9vad9.todolist.cli.commands.CreateCommand
import com.y9vad9.todolist.cli.commands.DeleteCommand
import com.y9vad9.todolist.cli.commands.EditCommand
import com.y9vad9.todolist.cli.commands.ListCommand
import com.y9vad9.todolist.cli.commands.MainCommand
import com.y9vad9.todolist.cli.commands.StartCommand
import com.y9vad9.todolist.cli.commands.ViewCommand
import com.y9vad9.todolist.cli.localization.EnglishStrings
import com.y9vad9.todolist.cli.localization.GermanStrings
import com.y9vad9.todolist.dependencies.initDependencies
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import com.y9vad9.todolist.integration.FileSystemSettingsRepository
import com.y9vad9.todolist.domain.type.settings.AppLanguage
import com.y9vad9.todolist.presentation.mvi.settings.types.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import okio.Path.Companion.toOkioPath
import org.koin.core.Koin
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.util.Locale
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.pathString

suspend fun main(args: Array<String>) {
    val driver = JdbcSqliteDriver(
        "jdbc:sqlite:${AppDirectory.pathString}/data.db",
    )
    val timeZoneRepository = object : TimeZoneRepository {
        override val timeZone: StateFlow<TimeZone> =
            MutableStateFlow(java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone())
    }

    val settingsRepository = FileSystemSettingsRepository(
        path = AppDirectory.resolve("settings.json").toFile().toOkioPath(),
        defaultSettings = AppSettings(
            when (Locale.getDefault()) {
                Locale.GERMAN -> AppLanguage.GERMAN
                else -> AppLanguage.ENGLISH
            }
        ),
    )

    val koin: Koin = initDependencies(
        sqlDriver = driver,
        timeZoneRepository = timeZoneRepository,
        clock = Clock.System,
        settingsRepository = settingsRepository,
    ).koin


    val strings = when (Locale.getDefault()) {
        Locale.GERMAN -> GermanStrings
        else -> EnglishStrings
    }

    return MainCommand()
        .subcommands(
            ListCommand(
                strings = strings,
                listAllTasksUseCase = koin.get(),
                clock = Clock.System
            ),
            ViewCommand(
                getTaskUseCase = koin.get(),
                strings = strings,
                clock = Clock.System
            ),
            StartCommand(
                moveScheduledTaskToInProgressUseCase = koin.get(),
                strings = strings,
            ),
            EditCommand(
                updateTaskUseCase = koin.get(),
                strings = strings,
            ),
            DeleteCommand(
                deleteTaskUseCase = koin.get(),
                strings = strings,
            ),
            CreateCommand(
                createTaskUseCase = koin.get(),
                strings = strings,
            ),
            CompleteCommand(
                moveInProgressToCompletedUseCase = koin.get(),
                strings = strings,
            )
        ).main(args)
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