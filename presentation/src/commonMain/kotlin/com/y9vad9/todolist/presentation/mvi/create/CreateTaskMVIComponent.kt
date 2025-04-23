package com.y9vad9.todolist.presentation.mvi.create

import com.arkivanov.decompose.ComponentContext
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.CreateTaskUseCase
import com.y9vad9.todolist.presentation.validation.input
import com.y9vad9.todolist.presentation.validation.mappers.LocalDateFromStringFactory
import com.y9vad9.todolist.presentation.validation.mappers.LocalTimeFromStringFactory
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.dsl.emit
import pro.respawn.flowmvi.essenty.api.DelicateRetainedApi
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import kotlinx.datetime.Instant
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import pro.respawn.flowmvi.dsl.updateStateImmediate

class CreateTaskMVIComponent(
    componentContext: ComponentContext,
    private val createTaskUseCase: CreateTaskUseCase,
    private val timeZoneRepository: TimeZoneRepository,
) : ComponentContext by componentContext, Container<CreateTaskComponentState, CreateTaskComponentIntent, CreateTaskComponentAction> {
    @OptIn(DelicateRetainedApi::class)
    override val store = retainedStore<CreateTaskComponentState, CreateTaskComponentIntent, CreateTaskComponentAction>(
        initial = CreateTaskComponentState()
    ) {
        recover { exception ->
            exception.printStackTrace()
            emit(CreateTaskComponentAction.ShowError(exception))
            null
        }

        reduce { intent ->
            updateStateImmediate {
                when (intent) {
                    is CreateTaskComponentIntent.DescriptionChanged ->
                        copy(description = input(intent.description))

                    is CreateTaskComponentIntent.DueDateChanged ->
                        copy(dueDate = input(intent.dueDate))

                    is CreateTaskComponentIntent.DueTimeChanged ->
                        copy(dueTime = input(intent.dueTime))

                    is CreateTaskComponentIntent.NameChanged ->
                        copy(name = input(intent.name))

                    CreateTaskComponentIntent.AddClicked -> {
                        validated().run {
                            if (isValid()) {

                                createAsync(
                                    name = TaskName.factory.createOrThrow(name.value),
                                    description = TaskDescription.factory.createOrThrow(description.value),
                                    dueDate = LocalDateFromStringFactory.createOrThrow(dueDate.value)
                                        .atTime(LocalTimeFromStringFactory.createOrThrow(dueTime.value))
                                        .toInstant(timeZoneRepository.timeZone.value),
                                )
                                copy(isLoading = true)
                            } else {
                                this
                            }
                        }
                    }
                }
            }
        }
    }

    private fun PipelineContext<CreateTaskComponentState, CreateTaskComponentIntent, CreateTaskComponentAction>.createAsync(
        name: TaskName,
        description: TaskDescription,
        dueDate: Instant,
    ) {
        launch {
            when (val result = createTaskUseCase.execute(name, description, dueDate)) {
                CreateTaskUseCase.Result.DueInPast -> {
                    emit(CreateTaskComponentAction.ShowDueInPastError)
                    updateStateImmediate { copy(isLoading = false) }
                }

                is CreateTaskUseCase.Result.Error ->
                    emit(CreateTaskComponentAction.ShowError(result.error))

                is CreateTaskUseCase.Result.Success ->
                    emit(CreateTaskComponentAction.NavigateOut(result.task.id.int))
            }
        }
    }
}