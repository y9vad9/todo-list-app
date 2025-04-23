package com.y9vad9.todolist.presentation.mvi.view

import com.arkivanov.decompose.ComponentContext
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.GetTaskUseCase
import com.y9vad9.todolist.domain.usecase.MoveInProgressToCompletedUseCase
import com.y9vad9.todolist.domain.usecase.MoveScheduledTaskToInProgressUseCase
import com.y9vad9.todolist.presentation.mvi.view.ViewTaskMVIAction.ShowError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.emit
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.essenty.api.DelicateRetainedApi
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce

class ViewTaskMVIComponent(
    componentContext: ComponentContext,
    private val id: TaskId,
    private val getTaskUseCase: GetTaskUseCase,
    private val moveScheduledTaskToInProgressUseCase: MoveScheduledTaskToInProgressUseCase,
    private val moveInProgressToCompletedUseCase: MoveInProgressToCompletedUseCase,
) : ComponentContext by componentContext, Container<ViewTaskMVIState, ViewTaskMVIIntent, ViewTaskMVIAction> {
    @OptIn(DelicateRetainedApi::class)
    override val store: Store<ViewTaskMVIState, ViewTaskMVIIntent, ViewTaskMVIAction> = retainedStore(
        initial = ViewTaskMVIState.Loading,
    ) {
        recover { exception ->
            emit(ShowError(exception))
            exception.printStackTrace()
            null
        }

        reduce { intent ->
            when (intent) {
                ViewTaskMVIIntent.MarkAsCompleted -> {
                    updateState {
                        if (this !is ViewTaskMVIState.Loaded)
                            return@updateState this

                        copy(isMoving = true)
                    }
                    markAsCompletedAsync()
                }

                ViewTaskMVIIntent.MarkAsInProgress -> {
                    updateState {
                        if (this !is ViewTaskMVIState.Loaded)
                            return@updateState this

                        copy(isMoving = true)
                    }
                    markAsInProgressAsync()
                }
            }
        }

        init {
            launch {
                getTaskUseCase.execute(id).collect { result ->
                    when (val result = getTaskUseCase.execute(id).first()) {
                        is GetTaskUseCase.Result.Error -> {
                            emit(ShowError(result.error))
                            updateState {
                                ViewTaskMVIState.Failure(result.error)
                            }
                        }

                        GetTaskUseCase.Result.NotFound ->
                            updateState { ViewTaskMVIState.NotFound }

                        is GetTaskUseCase.Result.Success -> updateState {
                            ViewTaskMVIState.Loaded(task = result.task, isMoving = false)
                        }
                    }
                }
            }
        }
    }

    private fun PipelineContext<ViewTaskMVIState, ViewTaskMVIIntent, ViewTaskMVIAction>.markAsCompletedAsync() {
        launch {
            when (val result = moveInProgressToCompletedUseCase.execute(id)) {
                is MoveInProgressToCompletedUseCase.Result.Error -> {
                    result.error.printStackTrace()
                    emit(ShowError(result.error))
                }

                MoveInProgressToCompletedUseCase.Result.NotFound -> {
                    updateState { ViewTaskMVIState.NotFound }
                }

                MoveInProgressToCompletedUseCase.Result.NotInProgress -> {
                    emit(ShowError(IllegalArgumentException("Task is not in progress")))
                }
                // we can ignore it, it should be state updated by now (might be
                // triggered by double-clicking)
                MoveInProgressToCompletedUseCase.Result.AlreadyCompleted -> {}
                is MoveInProgressToCompletedUseCase.Result.Success -> {}
            }
        }
    }

    private fun PipelineContext<ViewTaskMVIState, ViewTaskMVIIntent, ViewTaskMVIAction>.markAsInProgressAsync() {
        launch {
            when (val result = moveScheduledTaskToInProgressUseCase.execute(id)) {
                is MoveScheduledTaskToInProgressUseCase.Result.Error -> {
                    emit(ShowError(result.error))
                }

                MoveScheduledTaskToInProgressUseCase.Result.NotFound -> {
                    updateState { ViewTaskMVIState.NotFound }
                }
                // we can ignore it, it should be state updated by now (might be
                // triggered by double-clicking)
                MoveScheduledTaskToInProgressUseCase.Result.AlreadyCompleted,
                MoveScheduledTaskToInProgressUseCase.Result.AlreadyInProgress,
                is MoveScheduledTaskToInProgressUseCase.Result.Success -> {}
            }
        }
    }
}