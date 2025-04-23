package com.y9vad9.todolist.presentation.validation.mappers

import com.y9vad9.ktiny.kotlidator.CreationFailure
import com.y9vad9.ktiny.kotlidator.ValidationResult
import com.y9vad9.ktiny.kotlidator.ValueFactory
import com.y9vad9.ktiny.kotlidator.factory
import com.y9vad9.ktiny.kotlidator.rule.ValidationRule
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3

object LocalDateFromStringFactory : ValueFactory<LocalDate, String> by factory(
    rules = listOf(DateFormatValidationRule),
    constructor = { string ->
        val (dayStr, monthStr, yearStr) = string.split("/")
        val day = dayStr.toIntOrNull()
        val month = monthStr.toIntOrNull()
        val year = yearStr.toIntOrNull()

        LocalDate(year!!, month!!, day!!)
    }
)

object DateFormatValidationRule : ValidationRule<String> {
    private const val MIN_DAY = 1
    private const val MAX_DAY = 31
    private const val MIN_MONTH = 1
    private const val MAX_MONTH = 12
    private const val MIN_YEAR = 1

    override fun validate(value: String): ValidationResult {
        val parts = value.split("/")
        if (parts.size != 3) return ValidationResult.invalid(InvalidDateFormatError)

        val (dayStr, monthStr, yearStr) = parts
        val day = dayStr.toIntOrNull()
        val month = monthStr.toIntOrNull()
        val year = yearStr.toIntOrNull()

        if (day !in MIN_DAY..MAX_DAY || month !in MIN_MONTH..MAX_MONTH || year !in MIN_YEAR..Int.MAX_VALUE) {
            return ValidationResult.invalid(InvalidDateFormatError)
        }

        val maxDay = when (month) {
            2 -> if (isLeapYear(year!!)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }

        return if (day!! > maxDay) {
            ValidationResult.invalid(InvalidDateFormatError)
        } else {
            ValidationResult.valid()
        }
    }

    private fun isLeapYear(year: Int): Boolean =
        (year and 3 == 0) && (year % 100 != 0 || year % 400 == 0)
}

data object InvalidDateFormatError : CreationFailure {
    override val message: String = "Invalid date format (should be dd/MM/yyyy) or date does not exist."
}

fun Instant.toStringRepresentation(timeZone: TimeZone): String {
    val localDateTime = this.toLocalDateTime(timeZone)

    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val year = localDateTime.year
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$day/$month/$year $hour:$minute"
}
