package com.y9vad9.todolist.presentation.mvi.view

import pro.respawn.flowmvi.api.MVIAction

sealed interface ViewTaskMVIAction : MVIAction {
    data class ShowError(val throwable: Throwable) : ViewTaskMVIAction
}