package com.y9vad9.todolist.presentation.mvi.list

import pro.respawn.flowmvi.api.MVIAction

sealed interface ListTasksMVIAction : MVIAction {
    data class ShowError(val throwable: Throwable) : ListTasksMVIAction
}