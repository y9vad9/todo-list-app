package com.y9vad9.todolist.composeui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.composeui.localization.LocalTimeZone
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.presentation.mvi.view.ViewTaskMVIAction
import com.y9vad9.todolist.presentation.mvi.view.ViewTaskMVIIntent
import com.y9vad9.todolist.presentation.mvi.view.ViewTaskMVIState
import com.y9vad9.todolist.presentation.validation.mappers.toStringRepresentation
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.compose.dsl.subscribe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTaskScreenComponent(
    container: Container<ViewTaskMVIState, ViewTaskMVIIntent, ViewTaskMVIAction>,
    onEdit: () -> Unit,
    onBack: () -> Unit,
) {
    val snackbarData = remember { SnackbarHostState() }
    val strings = LocalStrings.current

    val state by container.store.subscribe { action ->
        when (action) {
            is ViewTaskMVIAction.ShowError -> snackbarData.showSnackbar(
                message = strings.internalErrorMessage(action.throwable),
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = strings.goBackActionDescription
                        )
                    }
                },
                title = { Text(text = strings.viewTaskTitle) },
                actions = {
                    IconButton(
                        onClick = onEdit,
                        enabled = state !is ViewTaskMVIState.Loading,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = strings.editTaskButtonDescription,
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarData) { Snackbar(it) }
        },
    ) { paddingValues ->
        if (state is ViewTaskMVIState.Loaded) {
            val state = state as ViewTaskMVIState.Loaded

            Box(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = state.task.name.string,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedCard(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = strings.dueToTitle,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = state.task.due.toStringRepresentation(LocalTimeZone.current),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }

                        OutlinedCard(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = strings.createdAtTitle,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = state.task.createdAt.toStringRepresentation(LocalTimeZone.current),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.task !is CompletedTask) {
                            OutlinedCard(modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = strings.statusTitle,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = when (state.task) {
                                            is CompletedTask -> LocalStrings.current.completedTitle
                                            is InProgressTask -> LocalStrings.current.inProgressTitle
                                            is PlannedTask -> LocalStrings.current.planedTitle
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }

                        if (state.task !is PlannedTask) {
                            val (title, sub) = when (state.task) {
                                is CompletedTask -> strings.startedAtTitle to (state.task as CompletedTask)
                                    .completedAt
                                    .toStringRepresentation(LocalTimeZone.current)

                                is InProgressTask -> strings.startedAtTitle to (state.task as InProgressTask)
                                    .startedAt
                                    .toStringRepresentation(LocalTimeZone.current)

                                else -> return@Row
                            }

                            OutlinedCard(modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = sub,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }

                        if (state.task is CompletedTask) {
                            OutlinedCard(modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = strings.completedTitle,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = (state.task as CompletedTask).completedAt.toStringRepresentation(
                                            LocalTimeZone.current
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }

                    Markdown(
                        content = state.task.description.string,
                        modifier = Modifier.fillMaxWidth(),
                        typography = markdownTypography(
                            h1 = MaterialTheme.typography.headlineSmall,
                            h2 = MaterialTheme.typography.titleLarge,
                            h3 = MaterialTheme.typography.titleMedium,
                            h4 = MaterialTheme.typography.titleSmall,
                            h5 = MaterialTheme.typography.bodyLarge,
                            h6 = MaterialTheme.typography.bodyMedium,
                        ),
                        imageTransformer = Coil3ImageTransformerImpl,
                    )

                    if (state.task !is CompletedTask) {
                        Spacer(Modifier.height(ButtonDefaults.MinHeight))
                    }
                }

                if (state.task !is CompletedTask) {
                    Button(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        onClick = {
                            if (state.task is InProgressTask)
                                container.store.intent(ViewTaskMVIIntent.MarkAsCompleted)
                            else container.store.intent(ViewTaskMVIIntent.MarkAsInProgress)
                        },
                    ) {
                        Text(
                            if (state.task is InProgressTask)
                                strings.markAsCompletedButton
                            else strings.markAsInProgressButton,
                        )
                    }
                }
            }
        }
    }
}