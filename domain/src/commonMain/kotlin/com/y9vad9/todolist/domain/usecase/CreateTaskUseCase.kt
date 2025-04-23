package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.PlannedTask
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class CreateTaskUseCase(
    private val repo: TaskRepository,
    private val clock: Clock,
) {
    sealed interface Result {
        data class Success(val task: PlannedTask) : Result
        data class Error(val error: Throwable) : Result
        data object DueInPast : Result
    }

    suspend fun execute(
        name: TaskName,
        description: TaskDescription,
        due: Instant
    ): Result {
        val createdAt = clock.now()
        if (due < createdAt) return Result.DueInPast

        return try {
            val task = repo.create(name, description, createdAt, due)
            Result.Success(task)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }
}
