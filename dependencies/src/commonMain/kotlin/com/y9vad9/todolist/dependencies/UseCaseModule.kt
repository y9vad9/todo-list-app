package com.y9vad9.todolist.dependencies

import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import com.y9vad9.todolist.domain.usecase.CreateTaskUseCase
import com.y9vad9.todolist.domain.usecase.DeleteTaskUseCase
import com.y9vad9.todolist.domain.usecase.GetImportantTasksUseCase
import com.y9vad9.todolist.domain.usecase.GetTaskUseCase
import com.y9vad9.todolist.domain.usecase.ListAllTasksUseCase
import com.y9vad9.todolist.domain.usecase.MoveInProgressToCompletedUseCase
import com.y9vad9.todolist.domain.usecase.MoveScheduledTaskToInProgressUseCase
import com.y9vad9.todolist.domain.usecase.UpdateTaskUseCase
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlinx.datetime.Clock

@Module
class UseCaseModule {
    @Single
    fun createTaskUseCase(
        repository: TaskRepository,
        clock: Clock,
    ): CreateTaskUseCase {
        return CreateTaskUseCase(
            repo = repository,
            clock = clock,
        )
    }

    @Single
    fun deleteTaskUseCase(
        repository: TaskRepository,
    ): DeleteTaskUseCase {
        return DeleteTaskUseCase(repository)
    }

    @Single
    fun updateTaskUseCase(
        repository: TaskRepository,
        clock: Clock,
    ): UpdateTaskUseCase {
        return UpdateTaskUseCase(repository, clock)
    }

    @Single
    fun listAllTasksUseCase(
        repository: TaskRepository,
    ): ListAllTasksUseCase {
        return ListAllTasksUseCase(repository)
    }

    @Single
    fun moveScheduledTaskToInProgressUseCase(
        repository: TaskRepository,
        clock: Clock,
    ): MoveScheduledTaskToInProgressUseCase {
        return MoveScheduledTaskToInProgressUseCase(repository, clock)
    }

    @Single
    fun moveInProgressToCompletedUseCase(
        repository: TaskRepository,
        clock: Clock,
    ): MoveInProgressToCompletedUseCase {
        return MoveInProgressToCompletedUseCase(repository, clock)
    }

    @Single
    fun getTaskUseCase(
        repository: TaskRepository,
    ): GetTaskUseCase {
        return GetTaskUseCase(repository)
    }

    @Single
    fun getImportantTasksUseCase(
        timeZoneRepository: TimeZoneRepository,
        tasksRepository: TaskRepository,
        clock: Clock,
    ): GetImportantTasksUseCase {
        return GetImportantTasksUseCase(timeZoneRepository, tasksRepository, clock)
    }
}