package com.y9vad9.todolist.composeui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.y9vad9.todolist.composeui.component.TaskItemComponent
import com.y9vad9.todolist.composeui.component.TaskItemComponentSkeleton
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.domain.type.TaskListType
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.presentation.mvi.list.ListTasksMVIAction
import com.y9vad9.todolist.presentation.mvi.list.ListTasksMVIIntent
import com.y9vad9.todolist.presentation.mvi.list.ListTasksMVIState
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.compose.dsl.subscribe

@Composable
fun ListTasksScreenComponent(
    container: Container<ListTasksMVIState, ListTasksMVIIntent, ListTasksMVIAction>,
    onTaskClicked: (TaskId) -> Unit,
) {
    val snackbarData = remember { SnackbarHostState() }
    val strings = LocalStrings.current

    val state by container.store.subscribe { action ->
        when (action) {
            is ListTasksMVIAction.ShowError ->
                snackbarData.showSnackbar(message = strings.internalErrorMessage(action.throwable))
        }
    }

    val isLoading = state is ListTasksMVIState.Loading
    val selectedCategories: List<TaskListType> = (state as? ListTasksMVIState.Loaded)?.selectedCategories ?: emptyList()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarData,
            ) {
                Snackbar(it)
            }
        },
    ) { _ ->
        LazyColumn(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "filters") {
                Column {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(LocalStrings.current.filterTitle)
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        },
                        value = (state as? ListTasksMVIState.Loaded)?.filter ?: "",
                        onValueChange = {
                            container.store.intent(ListTasksMVIIntent.FilterUpdate(it))
                        },
                        singleLine = true,
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item(key = "scheduled") {
                            FilterChip(
                                selected = TaskListType.SCHEDULED in selectedCategories,
                                label = {
                                    Text(strings.planedTitle)
                                },
                                onClick = {
                                    container.store.intent(ListTasksMVIIntent.CategoryToggle(TaskListType.SCHEDULED))
                                }
                            )
                        }

                        item(key = "in_progress") {
                            FilterChip(
                                selected = TaskListType.IN_PROGRESS in selectedCategories,
                                label = {
                                    Text(strings.inProgressTitle)
                                },
                                onClick = {
                                    container.store.intent(ListTasksMVIIntent.CategoryToggle(TaskListType.IN_PROGRESS))
                                }
                            )
                        }

                        item(key = "completed") {
                            FilterChip(
                                selected = TaskListType.COMPLETED in selectedCategories,
                                label = {
                                    Text(strings.completedTitle)
                                },
                                onClick = {
                                    container.store.intent(ListTasksMVIIntent.CategoryToggle(TaskListType.COMPLETED))
                                }
                            )
                        }
                    }
                }
            }

            if (state is ListTasksMVIState.Loaded) {
                if ((state as ListTasksMVIState.Loaded).tasks.isNotEmpty()) {
                    items((state as ListTasksMVIState.Loaded).tasks, key = { it.id.int }) {
                        TaskItemComponent(
                            task = it,
                            onClick = {
                                onTaskClicked(it.id)
                            }
                        )
                    }
                } else {
                    item {
                        NoItems()
                    }
                }
            } else {
                items(3) {
                    TaskItemComponentSkeleton()
                }
            }

            item(key = "fab_placeholder") {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NoItems() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(LocalStrings.current.noItemsMessage)
    }
}