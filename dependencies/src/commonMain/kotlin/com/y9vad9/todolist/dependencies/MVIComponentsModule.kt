package com.y9vad9.todolist.dependencies

import com.arkivanov.decompose.ComponentContext
import com.y9vad9.todolist.domain.repository.SettingsRepository
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.*
import com.y9vad9.todolist.presentation.mvi.create.CreateTaskMVIComponent
import com.y9vad9.todolist.presentation.mvi.edit.EditTaskMVIComponent
import com.y9vad9.todolist.presentation.mvi.important.ImportantTasksMVIComponent
import com.y9vad9.todolist.presentation.mvi.list.ListTasksMVIComponent
import com.y9vad9.todolist.presentation.mvi.settings.SettingsMVIComponent
import com.y9vad9.todolist.presentation.mvi.view.ViewTaskMVIComponent
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class MVIComponentsModule {
    @Factory
    fun createTaskMVIComponent(
        componentContext: ComponentContext,
        createTaskUseCase: CreateTaskUseCase,
        timeZoneRepository: TimeZoneRepository
    ): CreateTaskMVIComponent {
        return CreateTaskMVIComponent(
            componentContext,
            createTaskUseCase,
            timeZoneRepository,
        )
    }

    @Factory
    fun editTaskMVIComponent(
        componentContext: ComponentContext,
        taskId: TaskId,
        updateTaskUseCase: UpdateTaskUseCase,
        getTaskUseCase: GetTaskUseCase,
        deleteTaskUseCase: DeleteTaskUseCase,
        timeZoneProvider: TimeZoneRepository,
    ): EditTaskMVIComponent {
        return EditTaskMVIComponent(
            componentContext,
            taskId,
            updateTaskUseCase,
            getTaskUseCase,
            deleteTaskUseCase,
            timeZoneProvider,
        )
    }

    @Factory
    fun importantTasksMviComponent(
        componentContext: ComponentContext,
        getImportantTasksUseCase: GetImportantTasksUseCase,
    ): ImportantTasksMVIComponent {
        return ImportantTasksMVIComponent(
            componentContext,
            getImportantTasksUseCase,
        )
    }

    @Factory
    fun listTasksMVIComponent(
        componentContext: ComponentContext,
        getAllTasksUseCase: ListAllTasksUseCase,
    ): ListTasksMVIComponent {
        return ListTasksMVIComponent(
            componentContext,
            getAllTasksUseCase
        )
    }

    @Factory
    fun settingsMVIComponent(
        componentContext: ComponentContext,
        settingsProvider: SettingsRepository,
    ): SettingsMVIComponent {
        return SettingsMVIComponent(
            componentContext,
            settingsProvider,
        )
    }

    @Factory
    fun viewTaskMVIComponent(
        componentContext: ComponentContext,
        id: TaskId,
        getTaskUseCase: GetTaskUseCase,
        moveScheduledTaskToInProgressUseCase: MoveScheduledTaskToInProgressUseCase,
        moveInProgressToCompletedUseCase: MoveInProgressToCompletedUseCase,
    ): ViewTaskMVIComponent {
        return ViewTaskMVIComponent(
            componentContext,
            id,
            getTaskUseCase,
            moveScheduledTaskToInProgressUseCase,
            moveInProgressToCompletedUseCase
        )
    }
}