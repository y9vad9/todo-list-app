package com.y9vad9.todolist.dependencies

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import com.y9vad9.todolist.database.TodoListDatabase
import com.y9vad9.todolist.domain.repository.SettingsRepository
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module

suspend fun initDependencies(
    sqlDriver: SqlDriver,
    timeZoneRepository: TimeZoneRepository,
    settingsRepository: SettingsRepository,
    clock: Clock,
    createDatabase: Boolean = true,
): KoinApplication {
    if (createDatabase) {
        try {
            TodoListDatabase.Schema.awaitCreate(sqlDriver)
        } catch (_: Exception) {
            // ignoring the error, assuming that schema is already created.
        }
    }

    return startKoin {
        val appModule = module {
            single {
                TodoListDatabase(sqlDriver)
            }

            single<SettingsRepository> { settingsRepository }

            single<TimeZoneRepository> {
                timeZoneRepository
            }

            single {
                clock
            }
        }

        modules(
            appModule,
            UseCaseModule().module,
            DataModule().module,
            MVIComponentsModule().module
        )
    }
}