package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.Task
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

class UpdateTaskUseCase(
    private val repo: TaskRepository,
    private val clock: Clock,
) {
    sealed interface Result {
        data class Success(val task: Task) : Result
        object NotFound : Result
        object DueInPast : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(
        id: TaskId,
        name: TaskName? = null,
        description: TaskDescription? = null,
        due: Instant? = null,
    ): Result {
        return try {
            if (due != null && due < clock.now()) {
                return Result.DueInPast
            }

            repo.update(id, name, description, due)
                ?.let { Result.Success(it) }
                ?: Result.NotFound
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }
}
