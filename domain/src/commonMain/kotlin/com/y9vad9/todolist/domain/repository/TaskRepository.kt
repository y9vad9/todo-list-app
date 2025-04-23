package com.y9vad9.todolist.domain.repository

import com.y9vad9.todolist.domain.type.*
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TaskRepository {
    /**
     * Creates a new scheduled task.
     */
    suspend fun create(
        name: TaskName,
        description: TaskDescription,
        createdAt: Instant,
        due: Instant,
    ): PlannedTask

    /**
     * Returns a task by its unique identifier.
     */
    suspend fun getById(id: TaskId): Flow<Task?>

    /**
     * Updates the name and/or description of any task.
     */
    suspend fun update(
        id: TaskId,
        name: TaskName? = null,
        description: TaskDescription? = null,
        due: Instant? = null,
    ): Task?

    /**
     * Deletes the task with the specified ID.
     */
    suspend fun delete(id: TaskId): Boolean

    /**
     * Returns all tasks of specified types. Returns all categories if
     * input [categories] is empty.
     */
    fun getAll(filter: String = "", categories: List<TaskListType> = emptyList()): Flow<List<Task>>

    fun getTasksWithDueBetween(timeRange: ClosedRange<Instant>): Flow<List<Task>>

    /**
     * Returns tasks with due date before the given instant.
     */
    suspend fun getDueTasks(before: Instant): Flow<List<Task>>

    /**
     * Returns all tasks created after the given timestamp.
     */
    suspend fun getCreatedAfter(timestamp: Instant): List<Task>

    /**
     * Moves a scheduled task to in-progress.
     * Returns null if the task is not found or not a scheduled task.
     */
    suspend fun moveToInProgress(
        id: TaskId,
        startedAt: Instant,
    ): InProgressTask?

    /**
     * Moves an in-progress task to completed.
     * Returns null if the task is not found or not in-progress.
     */
    suspend fun moveToCompleted(
        id: TaskId,
        completedAt: Instant,
    ): CompletedTask?
}
