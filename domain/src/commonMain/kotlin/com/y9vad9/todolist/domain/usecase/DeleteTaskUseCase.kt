package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.value.TaskId
import kotlin.uuid.Uuid

class DeleteTaskUseCase(
    private val repo: TaskRepository
) {
    sealed interface Result {
        object Success : Result
        object NotFound : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(id: TaskId): Result = try {
        if (repo.delete(id)) Result.Success else Result.NotFound
    } catch (t: Throwable) {
        Result.Error(t)
    }
}