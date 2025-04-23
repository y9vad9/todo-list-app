package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskId
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class MoveScheduledTaskToInProgressUseCase(
    private val repo: TaskRepository,
    private val clock: Clock,
) {
    sealed interface Result {
        data class Success(val task: InProgressTask) : Result
        object NotFound : Result
        data object AlreadyInProgress : Result
        data object AlreadyCompleted : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(
        id: TaskId,
    ): Result = try {
        when (repo.getById(id).first()) {
            null -> Result.NotFound

            is PlannedTask -> {
                repo.moveToInProgress(id, clock.now())
                    ?.let { Result.Success(it) }
                    ?: Result.AlreadyInProgress
            }

            is InProgressTask -> Result.AlreadyInProgress
            is CompletedTask -> Result.AlreadyCompleted
        }
    } catch (t: Throwable) {
        Result.Error(t)
    }
}