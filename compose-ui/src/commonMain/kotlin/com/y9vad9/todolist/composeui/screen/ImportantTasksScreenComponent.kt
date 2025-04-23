package com.y9vad9.todolist.composeui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.y9vad9.todolist.composeui.component.TaskItemComponent
import com.y9vad9.todolist.composeui.component.TaskItemComponentSkeleton
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.presentation.mvi.important.ImportantMVIAction
import com.y9vad9.todolist.presentation.mvi.important.ImportantMVIState
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.compose.dsl.subscribe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportantTasksComponent(
    mvi: Container<ImportantMVIState, MVIIntent, ImportantMVIAction>,
    onTaskClick: (id: TaskId) -> Unit,
) {
    val snackbarData = remember { SnackbarHostState() }
    val strings = LocalStrings.current

    val state by mvi.store.subscribe { action ->
        when (action) {
            is ImportantMVIAction.ShowError -> snackbarData.showSnackbar(
                message = strings.internalErrorMessage(action.throwable)
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarData,
            ) {
                Snackbar(it)
            }
        }
    ) {
        when (state) {
            ImportantMVIState.Failure -> Failure()
            is ImportantMVIState.Loaded ->
                LoadedItems(
                    state = state as ImportantMVIState.Loaded,
                    onTaskClick = onTaskClick,
                )

            ImportantMVIState.Loading -> ItemsSkeleton()
        }
    }
}

@Composable
private fun LoadedItems(
    state: ImportantMVIState.Loaded,
    onTaskClick: (id: TaskId) -> Unit,
) {
    if (state.dueTasks.isNotEmpty() || state.tasksNextDay.isNotEmpty() || state.tasksThisWeek.isNotEmpty() || state.tasksNextWeek.isNotEmpty()) {
        LazyColumn(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.dueTasks.isNotEmpty()) {
                item("due_tasks_header") {
                    Text(
                        text = LocalStrings.current.overdueTasksTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(state.dueTasks) {
                    TaskItemComponent(
                        task = it,
                        onClick = { onTaskClick(it.id) },
                    )
                }
            }

            if (state.tasksThisDay.isNotEmpty()) {
                item("this_day_tasks_header") {
                    Text(
                        text = LocalStrings.current.tasksDueTodayTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(state.tasksThisDay, key = { it.id.int }) {
                    TaskItemComponent(
                        task = it,
                        onClick = { onTaskClick(it.id) },
                    )
                }
            }

            if (state.tasksNextDay.isNotEmpty()) {
                item("next_day_tasks_header") {
                    Text(
                        text = LocalStrings.current.tasksUntilTomorrowTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(state.tasksNextDay, key = { it.id.int }) {
                    TaskItemComponent(
                        task = it,
                        onClick = { onTaskClick(it.id) },
                    )
                }
            }

            if (state.tasksThisWeek.isNotEmpty()) {
                item("this_week_tasks_header") {
                    Text(
                        text = LocalStrings.current.tasksThisWeekTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(state.tasksThisWeek, key = { it.id.int }) {
                    TaskItemComponent(
                        task = it,
                        onClick = { onTaskClick(it.id) },
                    )
                }
            }

            if (state.tasksNextWeek.isNotEmpty()) {
                item("next_week_tasks_header") {
                    Text(
                        text = LocalStrings.current.tasksNextWeekTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(state.tasksNextWeek, key = { it.id.int }) {
                    TaskItemComponent(
                        task = it,
                        onClick = { onTaskClick(it.id) },
                    )
                }
            }
        }
    } else {
        NoItems()
    }
}

@Composable
private fun ItemsSkeleton() {
    LazyColumn(
        Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item("due_tasks_header") {
            Text(
                text = LocalStrings.current.overdueTasksTitle,
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            TaskItemComponentSkeleton()
        }

        item("due_tasks_header") {
            Text(
                text = LocalStrings.current.overdueTasksTitle,
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            TaskItemComponentSkeleton()
        }
        item("next_day_tasks_header") {
            Text(
                text = LocalStrings.current.tasksUntilTomorrowTitle,
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            TaskItemComponentSkeleton()
        }


        item("this_week_tasks_header") {
            Text(
                text = LocalStrings.current.tasksThisWeekTitle,
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            TaskItemComponentSkeleton()
        }

        item("next_week_tasks_header") {
            Text(
                text = LocalStrings.current.tasksNextWeekTitle,
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            TaskItemComponentSkeleton()
        }
    }
}

@Composable
private fun Failure() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(LocalStrings.current.failureMessage)
    }
}

@Composable
private fun NoItems() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = LocalStrings.current.noItemsInImportantYetMessage,
            textAlign = TextAlign.Center,
        )
    }
}