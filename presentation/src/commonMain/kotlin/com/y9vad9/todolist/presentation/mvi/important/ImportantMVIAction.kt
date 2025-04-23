package com.y9vad9.todolist.presentation.mvi.important

import pro.respawn.flowmvi.api.MVIAction

sealed interface ImportantMVIAction : MVIAction {
    data class ShowError(val throwable: Throwable) : ImportantMVIAction
}