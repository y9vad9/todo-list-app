package com.y9vad9.todolist.presentation.mvi.important

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.y9vad9.todolist.domain.usecase.GetImportantTasksUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.emit
import pro.respawn.flowmvi.essenty.api.DelicateRetainedApi
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.recover

class ImportantTasksMVIComponent(
    componentContext: ComponentContext,
    private val getImportantTasksUseCase: GetImportantTasksUseCase,
) : ComponentContext by componentContext, Container<ImportantMVIState, MVIIntent, ImportantMVIAction> {
    private val coroutineScope = componentContext.coroutineScope()

    @OptIn(DelicateRetainedApi::class)
    override val store: Store<ImportantMVIState, MVIIntent, ImportantMVIAction> = retainedStore(
        initial = ImportantMVIState.Loading
    ) {
        recover { exception ->
            emit(ImportantMVIAction.ShowError(exception))
            null
        }

        init {
            getImportantTasksUseCase.execute()
                .onEach { result ->
                    when (result) {
                        is GetImportantTasksUseCase.Result.Error -> {
                            emit(ImportantMVIAction.ShowError(result.error))
                            result.error.printStackTrace()
                            updateState { ImportantMVIState.Failure }
                        }
                        is GetImportantTasksUseCase.Result.Success -> {
                            updateState {
                                ImportantMVIState.Loaded(
                                    dueTasks = result.dueTasks,
                                    tasksThisDay = result.tasksToday,
                                    tasksNextDay = result.tasksNextDay,
                                    tasksThisWeek = result.tasksThisWeek,
                                    tasksNextWeek = result.tasksNextWeek,
                                )
                            }
                        }
                    }
                }
                .launchIn(coroutineScope)
        }
    }
}