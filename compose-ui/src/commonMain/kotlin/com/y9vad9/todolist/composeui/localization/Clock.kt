package com.y9vad9.todolist.composeui.localization

import androidx.compose.runtime.compositionLocalOf
import kotlinx.datetime.Clock

val LocalClock = compositionLocalOf<Clock> { error("Clock is not provided.") }