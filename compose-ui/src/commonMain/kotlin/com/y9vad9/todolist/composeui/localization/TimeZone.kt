package com.y9vad9.todolist.composeui.localization

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import kotlinx.datetime.TimeZone

val LocalTimeZone: ProvidableCompositionLocal<TimeZone> = compositionLocalOf {
    error("LocalTimeZone is not provided.")
}