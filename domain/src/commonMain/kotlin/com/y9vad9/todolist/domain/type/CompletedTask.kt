package com.y9vad9.todolist.domain.type

import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import kotlin.time.Duration
import kotlinx.datetime.Instant

data class CompletedTask(
    override val id: TaskId,
    override val name: TaskName,
    override val description: TaskDescription,
    override val createdAt: Instant,
    val startedAt: Instant,
    val completedAt: Instant,
    override val due: Instant,
) : Task {
    val timeSpent: Duration get() = completedAt - startedAt

    override fun isDue(currentTime: Instant): Boolean {
        return false
    }
}