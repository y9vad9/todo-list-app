package com.y9vad9.todolist.composeui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainScreen {
    data object Important : MainScreen
    data object AllTasks : MainScreen
    data object Settings : MainScreen
}

