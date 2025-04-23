package com.y9vad9.todolist.domain.repository

import com.y9vad9.todolist.presentation.mvi.settings.types.AppSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    suspend fun getSettings(): StateFlow<AppSettings>
    suspend fun setSettings(settings: AppSettings)
}