package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.Task
import com.y9vad9.todolist.domain.type.TaskListType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ListAllTasksUseCase(
    private val repo: TaskRepository
) {
    sealed interface Result {
        data class Success(val tasks: List<Task>) : Result
        data class Error(val error: Throwable) : Result
    }

    fun execute(filter: String, categories: List<TaskListType>): Flow<Result> =
        repo.getAll(filter, categories)
            .map<_, Result> { Result.Success(it) }
            .catch { emit(Result.Error(it)) }
}