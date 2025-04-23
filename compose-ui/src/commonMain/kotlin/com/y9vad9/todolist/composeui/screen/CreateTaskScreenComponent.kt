package com.y9vad9.todolist.composeui.screen

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
import androidx.compose.ui.window.DialogProperties
import com.y9vad9.todolist.composeui.ext.failureToMessage
import com.y9vad9.todolist.composeui.localization.LocalClock
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.composeui.localization.LocalTimeZone
import com.y9vad9.todolist.presentation.mvi.create.CreateTaskComponentAction
import com.y9vad9.todolist.presentation.mvi.create.CreateTaskComponentIntent
import com.y9vad9.todolist.presentation.mvi.create.CreateTaskComponentState
import com.y9vad9.todolist.presentation.validation.isInvalid
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.compose.dsl.subscribe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreenComponent(
    mvi: Container<CreateTaskComponentState, CreateTaskComponentIntent, CreateTaskComponentAction>,
    onBack: () -> Unit,
) {
    val snackbarData = remember { SnackbarHostState() }
    val clock = LocalClock.current

    // we don't much need actual time as we append 1 hour to it already. For most part it's done just
    // for user not to see 00:00
    val currentTime = remember { clock.now() }.toLocalDateTime(LocalTimeZone.current).time

    val strings = LocalStrings.current
    val tz = LocalTimeZone.current

    val state by mvi.store.subscribe { action ->
        when (action) {
            is CreateTaskComponentAction.NavigateOut -> onBack()
            CreateTaskComponentAction.ShowDueInPastError ->
                snackbarData.showSnackbar(message = strings.dateCannotBeInPastMessage)

            is CreateTaskComponentAction.ShowError ->
                snackbarData.showSnackbar(message = strings.internalErrorMessage(action.error))
        }
    }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(currentTime.hour, currentTime.minute)

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date
                        mvi.store.intent(CreateTaskComponentIntent.DueDateChanged("${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}"))
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

                    mvi.store.intent(CreateTaskComponentIntent.DueTimeChanged(formatted))
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
                    layoutType = TimePickerLayoutType.Vertical,
                )
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = strings.goBackActionDescription)
                    }
                },
                title = { Text(text = strings.createTaskTitle) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarData) { Snackbar(it) }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.taskNameTitle) },
                value = state.name.value,
                onValueChange = { mvi.store.intent(CreateTaskComponentIntent.NameChanged(it)) },
                singleLine = true,
                isError = state.name.isInvalid(),
                supportingText = {
                    if (state.name.isInvalid())
                        Text(state.name.failureToMessage())
                },
                enabled = !state.isLoading,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.taskDescriptionTitle) },
                value = state.description.value,
                onValueChange = { mvi.store.intent(CreateTaskComponentIntent.DescriptionChanged(it)) },
                isError = state.description.isInvalid(),
                supportingText = {
                    if (state.description.isInvalid())
                        Text(state.description.failureToMessage())
                },
                enabled = !state.isLoading,
                maxLines = 4,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.taskDateTitle) },
                value = state.dueDate.value,
                onValueChange = { },
                readOnly = false,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker.value = true }) {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = null)
                    }
                },
                isError = state.dueDate.isInvalid(),
                singleLine = true,
                supportingText = {
                    if (state.dueDate.isInvalid())
                        Text(state.dueDate.failureToMessage())
                },
                enabled = !state.isLoading,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.taskTimeOfTheDayTitle) },
                value = state.dueTime.value,
                onValueChange = {
                    mvi.store.intent(CreateTaskComponentIntent.DueTimeChanged(it))
                },
                readOnly = false,
                trailingIcon = {
                    IconButton(onClick = { showTimePicker.value = true }) {
                        Icon(Icons.Rounded.AccessTime, contentDescription = null)
                    }
                },
                isError = state.dueTime.isInvalid(),
                singleLine = true,
                supportingText = {
                    if (state.dueTime.isInvalid())
                        Text(state.dueTime.failureToMessage())
                },
                enabled = !state.isLoading,
            )

            Spacer(Modifier.weight(1f))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { mvi.store.intent(CreateTaskComponentIntent.AddClicked) },
                enabled = !state.isLoading,
            ) {
                Text(strings.createTaskButton)
            }
        }
    }
}

