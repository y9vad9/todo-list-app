package com.y9vad9.todolist.composeui.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.y9vad9.ktiny.kotlidator.rule.MaxNumberValueValidationRule
import com.y9vad9.ktiny.kotlidator.rule.MinValueValidationRule
import com.y9vad9.ktiny.kotlidator.rule.StringLengthRangeValidationRule
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.presentation.validation.Input
import com.y9vad9.todolist.presentation.validation.isInvalid
import com.y9vad9.todolist.presentation.validation.isValid
import com.y9vad9.todolist.presentation.validation.mappers.DateFormatValidationRule
import com.y9vad9.todolist.presentation.validation.mappers.InvalidDateFormatError

@Stable
@Composable
fun Input<*>.failureToMessage(): String {
    if (!isInvalid()) return ""

    val strings = LocalStrings.current

    return when (failure) {
        is MaxNumberValueValidationRule.Failure<*> ->
            strings.maxNumberValueFailure((failure as MaxNumberValueValidationRule.Failure<*>).size)

        is MinValueValidationRule.Failure<*> ->
            strings.minNumberValueFailure((failure as MinValueValidationRule.Failure<*>).size)

        is StringLengthRangeValidationRule.Failure ->
            strings.stringLengthRangeFailure((failure as StringLengthRangeValidationRule.Failure).range)

        is InvalidDateFormatError ->
            strings.invalidDateFormatFailure

        else -> strings.unknownError
    }
}