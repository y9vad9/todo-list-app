package com.y9vad9.todolist.composeui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface DetailScreen {
    data object AddTask : DetailScreen
    data class EditTask(val taskId: Int) : DetailScreen
    data class ViewTask(val taskId: Int) : DetailScreen
}