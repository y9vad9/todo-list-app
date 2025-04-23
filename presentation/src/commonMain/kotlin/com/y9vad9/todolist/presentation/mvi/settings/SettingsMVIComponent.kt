package com.y9vad9.todolist.presentation.mvi.settings

import com.arkivanov.decompose.ComponentContext
import com.y9vad9.todolist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.emit
import pro.respawn.flowmvi.essenty.api.DelicateRetainedApi
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce

class SettingsMVIComponent(
    componentContext: ComponentContext,
    private val settingsRepository: SettingsRepository,
) : ComponentContext by componentContext,
    Container<SettingsMVIState, SettingsMVIIntent, SettingsMVIAction> {
    @OptIn(DelicateRetainedApi::class)
    override val store: Store<SettingsMVIState, SettingsMVIIntent, SettingsMVIAction> =
        retainedStore(
            initial = SettingsMVIState.Loading,
        ) {
            recover { exception ->
                exception.printStackTrace()
                emit(SettingsMVIAction.ShowError(exception))
                null
            }

            init {
                launch {
                    settingsRepository.getSettings().collectLatest {
                        updateState {
                            SettingsMVIState.Loaded(it)
                        }
                    }
                }
            }

            reduce { intent ->
                when (intent) {
                    is SettingsMVIIntent.SetLanguage -> {
                        updateState {
                            if (this !is SettingsMVIState.Loaded)
                                return@updateState this

                            val settings = settings.copy(language = intent.language)

                            launch {
                                settingsRepository.setSettings(settings)
                            }

                            copy(settings = settings)
                        }
                    }

                    is SettingsMVIIntent.SetTheme -> {
                        updateState {
                            if (this !is SettingsMVIState.Loaded)
                                return@updateState this
                            val settings = settings.copy(theme = intent.theme)

                            launch {
                                settingsRepository.setSettings(settings)
                            }

                            copy(settings = settings)
                        }
                    }
                }
            }
        }
}