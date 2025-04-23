package com.y9vad9.todolist.presentation.mvi.view

import com.y9vad9.todolist.domain.type.Task
import pro.respawn.flowmvi.api.MVIState

sealed interface ViewTaskMVIState : MVIState {
    data object Loading : ViewTaskMVIState
    data class Loaded(val task: Task, val isMoving: Boolean) : ViewTaskMVIState
    data class Failure(val throwable: Throwable) : ViewTaskMVIState
    data object NotFound : ViewTaskMVIState
}