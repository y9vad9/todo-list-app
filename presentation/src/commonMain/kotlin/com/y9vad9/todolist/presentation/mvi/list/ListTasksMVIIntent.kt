package com.y9vad9.todolist.presentation.mvi.list

import com.y9vad9.todolist.domain.type.TaskListType
import pro.respawn.flowmvi.api.MVIIntent

sealed interface ListTasksMVIIntent : MVIIntent {
    data class FilterUpdate(val filter: String) : ListTasksMVIIntent
    data class CategoryToggle(val category: TaskListType) : ListTasksMVIIntent
}