package com.y9vad9.todolist.presentation.mvi.view

import pro.respawn.flowmvi.api.MVIIntent

sealed interface ViewTaskMVIIntent : MVIIntent {
    data object MarkAsCompleted : ViewTaskMVIIntent
    data object MarkAsInProgress : ViewTaskMVIIntent
}