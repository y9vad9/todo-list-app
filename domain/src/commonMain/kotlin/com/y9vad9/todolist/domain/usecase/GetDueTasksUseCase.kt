package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class GetDueTasksUseCase(private val repo: TaskRepository) {
    sealed interface Result {
        data class Success(val tasks: Flow<List<Task>>) : Result
        data class Error(val error: Throwable) : Result
    }
    suspend fun execute(before: Instant): Result = try {
        Result.Success(repo.getDueTasks(before))
    } catch (t: Throwable) {
        Result.Error(t)
    }
}