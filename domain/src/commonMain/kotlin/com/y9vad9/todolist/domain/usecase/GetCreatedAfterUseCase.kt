package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.Task
import kotlinx.datetime.Instant

class GetCreatedAfterUseCase(private val repo: TaskRepository) {
    sealed interface Result {
        data class Success(val tasks: List<Task>) : Result
        data class Error(val error: Throwable) : Result
    }
    suspend fun execute(timestamp: Instant): Result = try {
        Result.Success(repo.getCreatedAfter(timestamp))
    } catch (t: Throwable) {
        Result.Error(t)
    }
}