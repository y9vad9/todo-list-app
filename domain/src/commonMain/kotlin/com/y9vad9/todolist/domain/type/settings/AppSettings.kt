package com.y9vad9.todolist.presentation.mvi.settings.types

import com.y9vad9.todolist.domain.type.settings.AppLanguage

data class AppSettings(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM,
)