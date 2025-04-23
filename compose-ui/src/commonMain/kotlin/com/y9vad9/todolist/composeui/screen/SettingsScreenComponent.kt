package com.y9vad9.todolist.composeui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.y9vad9.todolist.composeui.ext.shimmerBackground
import com.y9vad9.todolist.composeui.localization.EnglishStrings
import com.y9vad9.todolist.composeui.localization.GermanStrings
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.presentation.mvi.settings.SettingsMVIAction
import com.y9vad9.todolist.presentation.mvi.settings.SettingsMVIIntent
import com.y9vad9.todolist.presentation.mvi.settings.SettingsMVIState
import com.y9vad9.todolist.domain.type.settings.AppLanguage
import com.y9vad9.todolist.presentation.mvi.settings.types.AppTheme
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.compose.dsl.subscribe

@Composable
fun SettingsScreenComponent(
    container: Container<SettingsMVIState, SettingsMVIIntent, SettingsMVIAction>,
) {
    val snackbarData = remember { SnackbarHostState() }
    val strings = LocalStrings.current

    val state by container.store.subscribe { action ->
        when (action) {
            is SettingsMVIAction.ShowError ->
                snackbarData.showSnackbar(message = strings.internalErrorMessage(action.error))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarData,
            ) {
                Snackbar(it)
            }
        },
    ) {
        SettingsView(container, state)
    }
}

@Composable
private fun SettingsView(
    container: Container<SettingsMVIState, SettingsMVIIntent, SettingsMVIAction>,
    state: SettingsMVIState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item("app_language") {
            when (state) {
                is SettingsMVIState.Loading -> {
                    SettingsItemLoading(LocalStrings.current.appLanguageTitle)
                }

                else -> {
                    ExpandableSettingsItem(
                        title = LocalStrings.current.appLanguageTitle,
                        subtitle = LocalStrings.current.currentLanguageTitle,
                        options = listOf(
                            EnglishStrings.currentLanguageTitle to AppLanguage.ENGLISH,
                            GermanStrings.currentLanguageTitle to AppLanguage.GERMAN
                        ),
                        onSelect = { container.store.intent(SettingsMVIIntent.SetLanguage(it)) }
                    )
                }
            }
        }

        item("app_theme") {
            when (state) {
                is SettingsMVIState.Loading -> {
                    SettingsItemLoading(LocalStrings.current.appThemeTitle)
                }

                is SettingsMVIState.Loaded -> {
                    val settings = state.settings
                    ExpandableSettingsItem(
                        title = LocalStrings.current.appThemeTitle,
                        subtitle = when (settings.theme) {
                            AppTheme.LIGHT -> LocalStrings.current.lightThemeTitle
                            AppTheme.DARK -> LocalStrings.current.darkThemeTitle
                            AppTheme.SYSTEM -> LocalStrings.current.systemThemeTitle
                        },
                        options = listOf(
                            LocalStrings.current.systemThemeTitle to AppTheme.SYSTEM,
                            LocalStrings.current.lightThemeTitle to AppTheme.LIGHT,
                            LocalStrings.current.darkThemeTitle to AppTheme.DARK
                        ),
                        onSelect = { container.store.intent(SettingsMVIIntent.SetTheme(it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> ExpandableSettingsItem(
    title: String,
    subtitle: String,
    options: List<Pair<String, T>>,
    onSelect: (T) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        SettingsItem(
            title = title,
            subtitle = subtitle,
            onClick = { isExpanded = !isExpanded }
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            options.forEach { (title, value) ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        isExpanded = false
                        onSelect(value)
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
            }

            Icon(
                modifier = Modifier.align(Alignment.CenterEnd),
                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun SettingsItemLoading(title: String) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Box(
                    Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp)
                        .shimmerBackground()
                )
            }

            Icon(
                modifier = Modifier.align(Alignment.CenterEnd),
                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = null,
            )
        }
    }
}