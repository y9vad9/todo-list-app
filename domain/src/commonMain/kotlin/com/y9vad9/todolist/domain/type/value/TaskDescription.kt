package com.y9vad9.todolist.domain.type.value

import com.y9vad9.ktiny.kotlidator.ValueFactory
import com.y9vad9.ktiny.kotlidator.factory
import com.y9vad9.ktiny.kotlidator.rule.StringLengthRangeValidationRule
import kotlin.jvm.JvmInline

@JvmInline
value class TaskDescription private constructor(val string: String) {
    companion object {
        val EMPTY = TaskDescription("")

        val LENGTH_RANGE: IntRange = 0..10000

        val factory: ValueFactory<TaskDescription, String> = factory(
            rules = listOf(StringLengthRangeValidationRule(LENGTH_RANGE)),
            constructor = { TaskDescription(it) },
        )
    }
}