package com.y9vad9.todolist.cli.ext

import com.y9vad9.todolist.cli.localization.Strings
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant

@OptIn(ExperimentalTime::class)
internal fun Instant.timeUntilDue(due: Instant, strings: Strings): String {
    val duration = due - this
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
            val localDateTime = java.time.LocalDateTime.parse(this, format)
                .toInstant(java.time.ZoneOffset.UTC)
            return localDateTime.toKotlinInstant()
        } catch (_: DateTimeParseException) {
            // try next
        }

        try {
            val localDate = java.time.LocalDate.parse(this, format)
            return localDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toKotlinInstant()
        } catch (_: DateTimeParseException) {
            // try next
        }
    }

    return null
}
