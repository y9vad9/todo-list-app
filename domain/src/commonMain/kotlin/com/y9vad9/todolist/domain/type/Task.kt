package com.y9vad9.todolist.domain.type

import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import kotlinx.datetime.Instant

sealed interface Task {
    val id: TaskId
    val name: TaskName
    val description: TaskDescription
    val createdAt: Instant
    val due: Instant

    fun isDue(currentTime: Instant): Boolean {
        return currentTime >= due
    }
}

