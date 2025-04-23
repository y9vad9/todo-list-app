package com.y9vad9.todolist.presentation.mvi.edit

import pro.respawn.flowmvi.api.MVIIntent

sealed interface EditTaskComponentIntent : MVIIntent {
    data class NameChanged(val name: String) : EditTaskComponentIntent
    data class DescriptionChanged(val description: String) : EditTaskComponentIntent
    data class DueDateChanged(val dueDate: String) : EditTaskComponentIntent
    data class DueTimeChanged(val dueTime: String) : EditTaskComponentIntent
    data object SaveClicked : EditTaskComponentIntent
    data object DeleteClicked : EditTaskComponentIntent
}