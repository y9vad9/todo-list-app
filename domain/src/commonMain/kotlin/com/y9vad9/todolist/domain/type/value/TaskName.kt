package com.y9vad9.todolist.domain.type.value

import com.y9vad9.ktiny.kotlidator.ValueFactory
import com.y9vad9.ktiny.kotlidator.factory
import com.y9vad9.ktiny.kotlidator.rule.StringLengthRangeValidationRule
import kotlin.jvm.JvmInline

@JvmInline
value class TaskName private constructor(val string: String) {
    companion object {
        val LENGTH_RANGE: IntRange = 1..100

        val factory: ValueFactory<TaskName, String> = factory(
            rules = listOf(StringLengthRangeValidationRule(LENGTH_RANGE)),
            constructor = { TaskName(it) },
        )
    }
}