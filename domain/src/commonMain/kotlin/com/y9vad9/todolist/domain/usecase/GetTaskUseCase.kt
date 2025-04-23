package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.Task
import com.y9vad9.todolist.domain.type.value.TaskId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetTaskUseCase(
    private val repo: TaskRepository,
) {
    sealed interface Result {
        data class Success(val task: Task) : Result
        object NotFound : Result
        data class Error(val error: Throwable) : Result
    }

    suspend fun execute(id: TaskId): Flow<Result> =
        repo.getById(id).map {
            it?.let { Result.Success(it) }
                ?: Result.NotFound
        }.catch {
            emit(Result.Error(it))
        }
}