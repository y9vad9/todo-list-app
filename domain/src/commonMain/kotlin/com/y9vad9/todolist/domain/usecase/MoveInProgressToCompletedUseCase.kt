package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.value.TaskId
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlinx.datetime.Instant

class MoveInProgressToCompletedUseCase(
    private val repo: TaskRepository,
    private val clock: Clock,
) {
    sealed interface Result {
        data class Success(val task: CompletedTask) : Result
        data object NotFound : Result
        data object NotInProgress : Result
        data object AlreadyCompleted : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(
        id: TaskId,
    ): Result = try {
        when (repo.getById(id).first()) {
            null -> Result.NotFound

            is InProgressTask -> {
                repo.moveToCompleted(id, clock.now())
                    ?.let { Result.Success(it) }
                    ?: Result.NotInProgress
            }

            is CompletedTask -> Result.AlreadyCompleted

            else -> Result.NotInProgress
        }
    } catch (t: Throwable) {
        Result.Error(t)
    }
}
