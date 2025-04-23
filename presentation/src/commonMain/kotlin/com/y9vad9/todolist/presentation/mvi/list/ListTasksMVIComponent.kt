package com.y9vad9.todolist.presentation.mvi.list

import com.arkivanov.decompose.ComponentContext
import com.y9vad9.todolist.domain.type.TaskListType
import com.y9vad9.todolist.domain.usecase.ListAllTasksUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.emit
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.essenty.api.DelicateRetainedApi
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce

class ListTasksMVIComponent(
    componentContext: ComponentContext,
    private val getAllTasksUseCase: ListAllTasksUseCase,
) : ComponentContext by componentContext, Container<ListTasksMVIState, ListTasksMVIIntent, ListTasksMVIAction> {
    @OptIn(DelicateRetainedApi::class, ExperimentalCoroutinesApi::class)
    override val store: Store<ListTasksMVIState, ListTasksMVIIntent, ListTasksMVIAction> = retainedStore(
        initial = ListTasksMVIState.Loading
    ) {
        recover { exception ->
            emit(ListTasksMVIAction.ShowError(exception))
            null
        }

        val categories: MutableStateFlow<List<TaskListType>> = MutableStateFlow(emptyList())
        val searchFilter: MutableStateFlow<String> = MutableStateFlow("")

        init {
            launch {
                categories.combine(searchFilter) { categoriesList, search ->
                    categoriesList to search
                }.flatMapLatest { (categories, searchFilter) ->
                    getAllTasksUseCase.execute(searchFilter, categories)
                }.onEach { result ->
                    when (result) {
                        is ListAllTasksUseCase.Result.Error -> {
                            emit(ListTasksMVIAction.ShowError(result.error))
                            updateState { ListTasksMVIState.Failure }
                        }

                        is ListAllTasksUseCase.Result.Success -> {
                            updateState {
                                if (this !is ListTasksMVIState.Loaded)
                                    ListTasksMVIState.Loaded(categories.value, searchFilter.value, result.tasks)
                                else copy(tasks = result.tasks)
                            }
                        }
                    }
                }.consume()
            }
        }

        reduce { intent ->
            when (intent) {
                is ListTasksMVIIntent.CategoryToggle -> {
                    updateState {
                        if (this !is ListTasksMVIState.Loaded)
                            return@updateState this

                        if (intent.category in this.selectedCategories) {
                            copy(selectedCategories = this.selectedCategories - intent.category)
                        } else {
                            copy(selectedCategories = this.selectedCategories + intent.category)
                        }.also {
                            categories.value = it.selectedCategories
                        }
                    }
                }

                is ListTasksMVIIntent.FilterUpdate -> {
                    // to avoid problems with text fields
                    updateStateImmediate {
                        if (this !is ListTasksMVIState.Loaded)
                            return@updateStateImmediate this

                        copy(filter = intent.filter).also {
                            searchFilter.value = it.filter
                        }
                    }
                }
            }
        }
    }

}