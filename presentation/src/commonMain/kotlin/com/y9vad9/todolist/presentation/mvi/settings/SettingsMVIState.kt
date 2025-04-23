package com.y9vad9.todolist.presentation.mvi.settings

import com.y9vad9.todolist.presentation.mvi.settings.types.AppSettings
import pro.respawn.flowmvi.api.MVIState

sealed interface SettingsMVIState : MVIState {
    data object Loading : SettingsMVIState
    data class Loaded(val settings: AppSettings) : SettingsMVIState
}