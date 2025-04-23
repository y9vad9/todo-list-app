package com.y9vad9.todolist.presentation.mvi.important

import com.y9vad9.todolist.domain.type.Task
import pro.respawn.flowmvi.api.MVIState

sealed interface ImportantMVIState : MVIState {
    data object Loading : ImportantMVIState
    data class Loaded(
        val dueTasks: List<Task>,
        val tasksThisDay: List<Task>,
        val tasksNextDay: List<Task>,
        val tasksThisWeek: List<Task>,
        val tasksNextWeek: List<Task>,
    ) : ImportantMVIState
    data object Failure : ImportantMVIState
}