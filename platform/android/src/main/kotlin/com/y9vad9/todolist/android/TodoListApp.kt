package com.y9vad9.todolist.android

import android.app.Application
import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.y9vad9.todolist.database.TodoListDatabase
import com.y9vad9.todolist.dependencies.initDependencies
import com.y9vad9.todolist.integration.FileSystemSettingsRepository
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okio.Path.Companion.toOkioPath
import org.koin.dsl.module

class TodoListApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val driver = AndroidSqliteDriver(
            schema = TodoListDatabase.Schema.synchronous(),
            context = this,
            name = "TodoListDatabase",
        )

        val settingsFile = applicationContext.filesDir.resolve("settings.json")

        if (!settingsFile.exists()) settingsFile.createNewFile()

        val dependencies = runBlocking {
            initDependencies(
                sqlDriver = driver,
                timeZoneRepository = AndroidTimeZoneRepository(this@TodoListApp),
                settingsRepository = FileSystemSettingsRepository(
                    path = settingsFile.toOkioPath()
                ),
                clock = Clock.System,
            )
        }
        dependencies.modules(
            modules = listOf(
                module {
                    single<Context> { applicationContext }
                }
            )
        )
    }
}