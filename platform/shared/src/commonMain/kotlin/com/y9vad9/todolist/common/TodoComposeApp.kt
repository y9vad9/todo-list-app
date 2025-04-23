package com.y9vad9.todolist.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.y9vad9.todolist.composeui.TodoComposeUi
import com.y9vad9.todolist.composeui.localization.EnglishStrings
import com.y9vad9.todolist.composeui.localization.GermanStrings
import com.y9vad9.todolist.composeui.localization.LocalClock
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.composeui.localization.LocalTimeZone
import com.y9vad9.todolist.composeui.navigation.LocalComponentContext
import com.y9vad9.todolist.domain.repository.SettingsRepository
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import com.y9vad9.todolist.domain.type.settings.AppLanguage
import com.y9vad9.todolist.presentation.mvi.settings.types.AppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.TimeZone
import org.koin.compose.LocalKoinApplication
import org.koin.core.Koin

@Composable
fun TodoComposeApp(
    koin: Koin,
    rootComponentContext: ComponentContext,
    windowSizeClass: WindowSizeClass,
) {
    var theme: AppTheme by remember { mutableStateOf(AppTheme.SYSTEM) }
    var language: AppLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }
    val localTimeZone: TimeZone by LocalKoinApplication.current
        .get<TimeZoneRepository>()
        .timeZone
        .collectAsState()
    val isSystemInDarkMode = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        val settingsProvider = koin.get<SettingsRepository>()
        settingsProvider.getSettings().collectLatest {
            theme = it.theme
            language = it.language
        }
    }

    val strings = remember(language) {
        when (language) {
            AppLanguage.ENGLISH -> EnglishStrings
            AppLanguage.GERMAN -> GermanStrings
        }
    }

    val colors = remember(theme, isSystemInDarkTheme()) {
        when (theme) {
            AppTheme.LIGHT -> lightColorScheme()
            AppTheme.DARK -> darkColorScheme()
            AppTheme.SYSTEM -> if (isSystemInDarkMode) darkColorScheme() else lightColorScheme()
        }
    }

    MaterialTheme(colorScheme = colors) {
        CompositionLocalProvider(
            LocalComponentContext provides rootComponentContext,
            LocalStrings provides strings,
            LocalClock provides LocalKoinApplication.current.get(),
            LocalTimeZone provides localTimeZone,
        ) {
            TodoComposeUi(windowSizeClass)
        }
    }
}