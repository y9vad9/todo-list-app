package com.y9vad9.todolist.presentation.validation.mappers

import com.y9vad9.ktiny.kotlidator.CreationFailure
import com.y9vad9.ktiny.kotlidator.ValidationResult
import com.y9vad9.ktiny.kotlidator.ValueFactory
import com.y9vad9.ktiny.kotlidator.factory
import com.y9vad9.ktiny.kotlidator.rule.ValidationRule
import kotlinx.datetime.LocalTime

object LocalTimeFromStringFactory : ValueFactory<LocalTime, String> by factory(
    rules = listOf(TimeFormatValidationRule),
    constructor = { string ->
        val (hourStr, minuteStr) = string.split(":")
        val hour = hourStr.toIntOrNull()
        val minute = minuteStr.toIntOrNull()

        LocalTime(hour!!, minute!!)
    }
)

object TimeFormatValidationRule : ValidationRule<String> {
    private const val MIN_HOUR = 0
    private const val MAX_HOUR = 23
    private const val MIN_MINUTE = 0
    private const val MAX_MINUTE = 59

    override fun validate(value: String): ValidationResult {
        val parts = value.split(":")
        if (parts.size != 2) return ValidationResult.invalid(InvalidTimeFormatError)

        val (hourStr, minuteStr) = parts
        val hour = hourStr.toIntOrNull()
        val minute = minuteStr.toIntOrNull()

        if (hour !in MIN_HOUR..MAX_HOUR || minute !in MIN_MINUTE..MAX_MINUTE) {
            return ValidationResult.invalid(InvalidTimeFormatError)
        }

        return ValidationResult.valid()
    }
}

data object InvalidTimeFormatError : CreationFailure {
    override val message: String = "Invalid time format (should be HH:mm) or values out of range."
}
