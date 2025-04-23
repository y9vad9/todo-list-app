package com.y9vad9.todolist.presentation.mvi.settings

import com.y9vad9.todolist.domain.type.settings.AppLanguage
import com.y9vad9.todolist.presentation.mvi.settings.types.AppTheme
import pro.respawn.flowmvi.api.MVIIntent

sealed interface SettingsMVIIntent : MVIIntent {
    data class SetLanguage(val language: AppLanguage) : SettingsMVIIntent
    data class SetTheme(val theme: AppTheme) : SettingsMVIIntent
}