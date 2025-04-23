package com.y9vad9.todolist.presentation.mvi.create

import pro.respawn.flowmvi.api.MVIAction

sealed interface CreateTaskComponentAction : MVIAction {
    data class ShowError(val error: Throwable) : CreateTaskComponentAction
    data class NavigateOut(val taskId: Int) : CreateTaskComponentAction
    data object ShowDueInPastError : CreateTaskComponentAction
}