package com.y9vad9.todolist.dependencies

import com.y9vad9.todolist.database.TodoListDatabase
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.integration.DatabaseTaskRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DataModule {
    @Single
    fun tasksRepository(
        database: TodoListDatabase
    ): TaskRepository {
        return DatabaseTaskRepository(database)
    }
}