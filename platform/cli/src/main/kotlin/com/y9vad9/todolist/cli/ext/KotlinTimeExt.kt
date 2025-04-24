package com.y9vad9.todolist.cli.ext

import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.Task
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal fun Task.timeUntilDue(currentTime: Instant, strings: Strings): String {
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

        return if (completedAt > due) strings.dueFor(text) else strings.wasCompletedEarlier(text)
    }

    val duration = due - currentTime
    val abs = duration.absoluteValue

    val formatted = when {
        abs >= 365.days -> strings.years((abs.inWholeDays / 365).toInt())
        abs >= 30.days -> strings.months((abs.inWholeDays / 30).toInt())
        abs >= 7.days -> strings.weeks((abs.inWholeDays / 7).toInt())
        abs.inWholeDays > 0 -> strings.days(abs.inWholeDays.toInt())
        abs.inWholeHours > 0 -> strings.hours(abs.inWholeHours.toInt())
        else -> strings.minutes(abs.inWholeMinutes.toInt())
    }

    return if (duration.isNegative()) {
        strings.dueFor(formatted) // e.g., "3 days due"
    } else {
        strings.dueIn(formatted) // e.g., "3 days left"
    }
}


fun String.parseToInstant(): Instant? {
    val formats = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH),
    )

    for (format in formats) {
        try {
            return java.time.LocalDateTime.parse(this, format)
                .toKotlinLocalDateTime()
                .toInstant(java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone())
        } catch (_: DateTimeParseException) {
            // try next
        }

        try {
            val localDate = java.time.LocalDate.parse(this, format)
            return localDate.atStartOfDay().toKotlinLocalDateTime()
                .toInstant(java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone())
        } catch (_: DateTimeParseException) {
            // try next
        }
    }

    return null
}

fun Instant.formatToLocalString(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    locale: Locale = Locale.getDefault(),
    pattern: String = "yyyy-MM-dd HH:mm"
): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    val javaLocalDateTime = java.time.LocalDateTime.of(
        localDateTime.date.year,
        localDateTime.date.monthNumber,
        localDateTime.date.dayOfMonth,
        localDateTime.time.hour,
        localDateTime.time.minute,
        localDateTime.time.second,
        localDateTime.time.nanosecond
    )

    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return javaLocalDateTime.format(formatter)
}
