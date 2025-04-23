package com.y9vad9.todolist.composeui.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import com.y9vad9.todolist.composeui.localization.LocalClock
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.composeui.localization.LocalTimeZone
import com.y9vad9.todolist.composeui.localization.Strings
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.Task
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun Task.dueToOrOverdueByText(): State<String> {
    val clock: Clock = LocalClock.current
    val strings: Strings = LocalStrings.current

    if (this is CompletedTask) {
        val diff = (completedAt - due).absoluteValue
        val text = when {
            diff < 60.seconds -> strings.seconds(diff.inWholeSeconds.toInt())
            diff < 60.minutes -> strings.minutes(diff.inWholeMinutes.toInt())
            diff < 24.hours -> strings.hours(diff.inWholeHours.toInt())
            diff < 7.days -> strings.days(diff.inWholeDays.toInt())
            diff < 30.days -> strings.weeks((diff.inWholeDays / 7).toInt())
            diff < 365.days -> strings.months((diff.inWholeDays / 30).toInt())
            else -> strings.years((diff.inWholeDays / 365).toInt())
        }

        val label = if (completedAt > due) strings.overdueBy(text) else strings.completedEarlyBy(text)
        return rememberUpdatedState(label)
    }

    // We'll emit an updated string every tick
    val result = produceState(initialValue = "") {
        while (true) {
            val now = clock.now()
            val diff: Duration
            val text: String
            val updateDelay: Duration

            if (isDue(now)) {
                diff = now - due
            } else {
                diff = due - now
            }

            text = when {
                diff < 60.seconds -> {
                    updateDelay = 1.seconds
                    strings.seconds(diff.inWholeSeconds.toInt())
                }

                diff < 60.minutes -> {
                    updateDelay = 1.minutes
                    strings.minutes(diff.inWholeMinutes.toInt())
                }

                diff < 24.hours -> {
                    updateDelay = 1.hours
                    strings.hours(diff.inWholeHours.toInt())
                }

                diff < 7.days -> {
                    updateDelay = 1.days
                    strings.days(diff.inWholeDays.toInt())
                }

                diff < 30.days -> {
                    updateDelay = 1.days
                    strings.weeks((diff.inWholeDays / 7).toInt())
                }

                diff < 365.days -> {
                    updateDelay = 1.days
                    strings.months((diff.inWholeDays / 30).toInt())
                }

                else -> {
                    updateDelay = 1.days
                    strings.years((diff.inWholeDays / 365).toInt())
                }
            }

            value = if (isDue(now)) {
                strings.overdueBy(text)
            } else {
                strings.dueIn(text)
            }

            delay(updateDelay)
        }
    }

    return result
}
