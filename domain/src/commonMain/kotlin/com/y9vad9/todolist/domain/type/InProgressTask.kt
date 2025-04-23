package com.y9vad9.todolist.domain.type

import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

data class InProgressTask(
    override val id: TaskId,
    override val name: TaskName,
    override val description: TaskDescription,
    override val createdAt: Instant,
    val startedAt: Instant,
    override val due: Instant,
) : Task