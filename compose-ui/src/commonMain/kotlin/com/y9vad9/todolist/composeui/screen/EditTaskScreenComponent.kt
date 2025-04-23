package com.y9vad9.todolist.composeui.screen

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.y9vad9.todolist.composeui.ext.failureToMessage
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.composeui.localization.LocalTimeZone
import com.y9vad9.todolist.presentation.mvi.edit.EditTaskComponentAction
import com.y9vad9.todolist.presentation.mvi.edit.EditTaskComponentIntent
import com.y9vad9.todolist.presentation.mvi.edit.EditTaskComponentState
import com.y9vad9.todolist.presentation.validation.isInvalid
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.compose.dsl.subscribe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreenComponent(
    mvi: Container<EditTaskComponentState, EditTaskComponentIntent, EditTaskComponentAction>,
    onBack: (deleted: Boolean) -> Unit,
) {
    val snackbarData = remember { SnackbarHostState() }
    val strings = LocalStrings.current
    val tz = LocalTimeZone.current

    val state by mvi.store.subscribe { action ->
        when (action) {
            is EditTaskComponentAction.NavigateOut -> onBack(action.wasDeleted)
            EditTaskComponentAction.ShowDueInPastError ->
                snackbarData.showSnackbar(message = strings.dateCannotBeInPastMessage)

            is EditTaskComponentAction.ShowError ->
                snackbarData.showSnackbar(message = strings.internalErrorMessage(action.error))

            EditTaskComponentAction.NotFound -> onBack(true)
        }
    }

    // Date and Time Picker States
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    // Date and Time Picker States
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBack(false) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = strings.goBackActionDescription)
                    }
                },
                title = { Text(text = strings.editTaskTitle) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarData) { Snackbar(it) }
        },
    ) { paddingValues ->
        if (state is EditTaskComponentState.Loaded) {
            val loadedState = state as EditTaskComponentState.Loaded

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.taskNameTitle) },
                    value = loadedState.name.value,
                    onValueChange = {
                        mvi.store.intent(EditTaskComponentIntent.NameChanged(it))
                    },
                    singleLine = true,
                    isError = loadedState.name.isInvalid(),
                    supportingText = {
                        if (loadedState.name.isInvalid()) Text(loadedState.name.failureToMessage())
                    },
                    enabled = loadedState.isActionsAvailable,
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.taskDescriptionTitle) },
                    value = loadedState.description.value,
                    onValueChange = {
                        mvi.store.intent(EditTaskComponentIntent.DescriptionChanged(it))
                    },
                    isError = loadedState.description.isInvalid(),
                    supportingText = {
                        if (loadedState.description.isInvalid()) Text(loadedState.description.failureToMessage())
                    },
                    enabled = loadedState.isActionsAvailable,
                    maxLines = 4,
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.taskDateTitle) },
                    value = loadedState.dueDate.value,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker.value = true }) {
                            Icon(Icons.Rounded.CalendarToday, contentDescription = null)
                        }
                    },
                    isError = loadedState.dueDate.isInvalid(),
                    singleLine = true,
                    supportingText = {
                        if (loadedState.dueDate.isInvalid()) Text(loadedState.dueDate.failureToMessage())
                    },
                    enabled = loadedState.isActionsAvailable,
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.taskTimeOfTheDayTitle) },
                    value = loadedState.dueTime.value,
                    onValueChange = {
                        mvi.store.intent(EditTaskComponentIntent.DueTimeChanged(it))
                    },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker.value = true }) {
                            Icon(Icons.Rounded.AccessTime, contentDescription = null)
                        }
                    },
                    isError = loadedState.dueTime.isInvalid(),
                    singleLine = true,
                    supportingText = {
                        if (loadedState.dueTime.isInvalid()) Text(loadedState.dueTime.failureToMessage())
                    },
                    enabled = loadedState.isActionsAvailable,
                )

                // Show DatePicker Dialog
                if (showDatePicker.value) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker.value = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    val localDate = Instant.fromEpochMilliseconds(millis)
                                        .toLocalDateTime(tz).date
                                    mvi.store.intent(EditTaskComponentIntent.DueDateChanged("${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}"))
                                }
                                showDatePicker.value = false
                            }) {
                                Text(strings.confirmButton)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker.value = false }) {
                                Text(strings.cancelButton)
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (showTimePicker.value) {
                    AlertDialog(
                        onDismissRequest = { showTimePicker.value = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val hour = timePickerState.hour
                                val minute = timePickerState.minute
                                val formatted = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

                                mvi.store.intent(EditTaskComponentIntent.DueTimeChanged(formatted))
                                showTimePicker.value = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker.value = false }) {
                                Text("Cancel")
                            }
                        },
                        text = {
                            TimePicker(
                                modifier = Modifier.wrapContentSize(),
                                state = timePickerState,
                                layoutType = TimePickerLayoutType.Vertical
                            )
                        }
                    )
                }

                Spacer(Modifier.weight(1f))

                // Delete Button
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mvi.store.intent(EditTaskComponentIntent.DeleteClicked)
                    },
                    enabled = loadedState.isActionsAvailable,
                ) {
                    Text(strings.deleteTaskButton)
                }

                // Save Button
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mvi.store.intent(EditTaskComponentIntent.SaveClicked)
                    },
                    enabled = loadedState.isActionsAvailable,
                ) {
                    Text(strings.editTaskButton)
                }
            }
        }
    }
}
