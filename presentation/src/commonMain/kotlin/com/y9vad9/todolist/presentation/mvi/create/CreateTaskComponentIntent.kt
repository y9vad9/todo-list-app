package com.y9vad9.todolist.presentation.mvi.create

import pro.respawn.flowmvi.api.MVIIntent

sealed interface CreateTaskComponentIntent : MVIIntent {
    data class NameChanged(val name: String) : CreateTaskComponentIntent
    data class DescriptionChanged(val description: String) : CreateTaskComponentIntent
    data class DueDateChanged(val dueDate: String) : CreateTaskComponentIntent
    data class DueTimeChanged(val dueTime: String) : CreateTaskComponentIntent
    data object AddClicked : CreateTaskComponentIntent
}