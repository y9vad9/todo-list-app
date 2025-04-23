package com.y9vad9.todolist.presentation.mvi.list

import com.y9vad9.todolist.domain.type.Task
import com.y9vad9.todolist.domain.type.TaskListType
import pro.respawn.flowmvi.api.MVIState

sealed interface ListTasksMVIState : MVIState {
    data object Loading : ListTasksMVIState
    data class Loaded(
        val selectedCategories: List<TaskListType>,
        val filter: String,
        val tasks: List<Task>,
    ) : ListTasksMVIState
    data object Failure : ListTasksMVIState
}