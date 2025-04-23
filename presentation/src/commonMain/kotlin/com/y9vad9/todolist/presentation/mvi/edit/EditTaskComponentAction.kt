package com.y9vad9.todolist.presentation.mvi.edit

import pro.respawn.flowmvi.api.MVIAction

sealed interface EditTaskComponentAction : MVIAction {
    data class ShowError(val error: Throwable) : EditTaskComponentAction
    data class NavigateOut(val wasDeleted: Boolean) : EditTaskComponentAction
    data object ShowDueInPastError : EditTaskComponentAction
    data object NotFound : EditTaskComponentAction
}