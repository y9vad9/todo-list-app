package com.y9vad9.todolist.domain.type.value

import com.y9vad9.ktiny.kotlidator.factory
import com.y9vad9.ktiny.kotlidator.rule.MinValueValidationRule
import kotlin.jvm.JvmInline

@JvmInline
value class TaskId private constructor(val int: Int) {
    companion object {
        val factory = factory<TaskId, Int>(
            rules = listOf(MinValueValidationRule(0)),
            constructor = { TaskId(it) }
        )
    }
}