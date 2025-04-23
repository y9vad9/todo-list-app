package com.y9vad9.todolist.ios

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.y9vad9.todolist.database.TodoListDatabase
import com.y9vad9.todolist.dependencies.initDependencies
import com.y9vad9.todolist.integration.FileSystemSettingsRepository
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object Dependencies {
    private fun getAppSpecificDirectory(): Path {
        val fileManager = NSFileManager.defaultManager()
        val documentsURLs = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask) as? List<*>

        val documentsPath = (documentsURLs?.firstOrNull() as? platform.Foundation.NSURL)?.path
            ?: throw IllegalStateException("Failed to get Documents directory")

        val appSpecificDirectory = "$documentsPath/todolist"

        return appSpecificDirectory.toPath()
    }


    fun initIosDependencies(): Unit = runBlocking {
        initDependencies(
            sqlDriver = NativeSqliteDriver(
                schema = TodoListDatabase.Schema.synchronous(), name = "todo.db"
            ),
            timeZoneRepository = IosTimeZoneProvider(),
            clock = Clock.System,
            settingsRepository = FileSystemSettingsRepository(path = getAppSpecificDirectory()),
            createDatabase = false,
        )
    }
}