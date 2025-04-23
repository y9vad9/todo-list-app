package com.y9vad9.todolist.presentation.mvi.edit

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.DeleteTaskUseCase
import com.y9vad9.todolist.domain.usecase.GetTaskUseCase
import com.y9vad9.todolist.domain.usecase.UpdateTaskUseCase
import com.y9vad9.todolist.presentation.mvi.edit.EditTaskComponentAction.*
import com.y9vad9.todolist.presentation.validation.input
import com.y9vad9.todolist.presentation.validation.mappers.LocalDateFromStringFactory
import com.y9vad9.todolist.presentation.validation.mappers.LocalTimeFromStringFactory
import com.y9vad9.todolist.presentation.validation.mappers.toStringRepresentation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.dsl.emit
import pro.respawn.flowmvi.essenty.api.DelicateRetainedApi
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import kotlinx.datetime.Instant
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import pro.respawn.flowmvi.dsl.updateStateImmediate

class EditTaskMVIComponent(
    componentContext: ComponentContext,
    private val taskId: TaskId,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val timeZoneRepository: TimeZoneRepository,
) : ComponentContext by componentContext, Container<EditTaskComponentState, EditTaskComponentIntent, EditTaskComponentAction> {
    private val coroutineScope = componentContext.coroutineScope()

    @OptIn(DelicateRetainedApi::class)
    override val store = retainedStore<EditTaskComponentState, EditTaskComponentIntent, EditTaskComponentAction>(
        initial = EditTaskComponentState.Loading
    ) {
        recover { exception ->
            emit(ShowError(exception))
            null
        }

        init {
            when (val result = getTaskUseCase.execute(taskId).first()) {
                is GetTaskUseCase.Result.Error ->
                    emit(ShowError(result.error))

                GetTaskUseCase.Result.NotFound ->
                    updateState { EditTaskComponentState.NotFound }
                is GetTaskUseCase.Result.Success -> updateState {
                    val (date, time) = result.task.due.toStringRepresentation(timeZoneRepository.timeZone.value)
                        .split(" ")
                    EditTaskComponentState.Loaded(
                        name = input(result.task.name.string),
                        description = input(result.task.description.string),
                        dueDate = input(date),
                        dueTime = input(time)
                    )
                }
            }
        }

        reduce { intent ->
            updateStateImmediate {
                // ui shouldn't send intents until it's loaded, so
                // it's safe to ignore any incoming intents
                if (this !is EditTaskComponentState.Loaded) return@updateStateImmediate this

                when (intent) {
                    is EditTaskComponentIntent.DescriptionChanged ->
                        copy(description = input(intent.description))

                    is EditTaskComponentIntent.DueDateChanged ->
                        copy(dueDate = input(intent.dueDate))

                    is EditTaskComponentIntent.NameChanged ->
                        copy(name = input(intent.name))

                    is EditTaskComponentIntent.DueTimeChanged ->
                        copy(dueTime = input(intent.dueTime))

                    EditTaskComponentIntent.SaveClicked -> {
                        validated().run {
                            if (isValid()) {
                                createAsync(
                                    name = TaskName.factory.createOrThrow(name.value),
                                    description = TaskDescription.factory.createOrThrow(description.value),
                                    dueDate = LocalDateFromStringFactory.createOrThrow(dueDate.value)
                                        .atTime(LocalTimeFromStringFactory.createOrThrow(dueTime.value))
                                        .toInstant(timeZoneRepository.timeZone.value),
                                )
                                copy(isActionsAvailable = false)
                            } else {
                                this
                            }
                        }
                    }

                    EditTaskComponentIntent.DeleteClicked -> {
                        deleteAsync()
                        copy(isActionsAvailable = false)
                    }
                }
            }
        }
    }

    private fun PipelineContext<EditTaskComponentState, EditTaskComponentIntent, EditTaskComponentAction>.createAsync(
        name: TaskName,
        description: TaskDescription,
        dueDate: Instant,
    ) {
        launch {
            when (val result = updateTaskUseCase.execute(taskId, name, description, dueDate)) {
                UpdateTaskUseCase.Result.DueInPast ->
                    emit(ShowDueInPastError)

                is UpdateTaskUseCase.Result.Error ->
                    emit(ShowError(result.error))

                is UpdateTaskUseCase.Result.Success ->
                    emit(NavigateOut(wasDeleted = false))

                UpdateTaskUseCase.Result.NotFound ->
                    emit(NotFound)
            }
        }
    }

    private fun PipelineContext<EditTaskComponentState, EditTaskComponentIntent, EditTaskComponentAction>.deleteAsync() {
        launch {
            when (val result = deleteTaskUseCase.execute(taskId)) {
                is DeleteTaskUseCase.Result.Error ->
                    emit(ShowError(result.error))

                is DeleteTaskUseCase.Result.Success ->
                    emit(NavigateOut(wasDeleted = true))

                DeleteTaskUseCase.Result.NotFound ->
                    emit(NavigateOut(wasDeleted = true))
            }
        }
    }
}