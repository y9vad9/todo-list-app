package com.y9vad9.todolist.presentation.mvi.settings

import pro.respawn.flowmvi.api.MVIAction

sealed interface SettingsMVIAction : MVIAction {
    data class ShowError(val error: Throwable) : SettingsMVIAction
}