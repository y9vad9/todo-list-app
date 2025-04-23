package com.y9vad9.todolist.integration

import app.cash.sqldelight.coroutines.asFlow
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.database.TodoListDatabase
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.*
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import com.y9vad9.todolist.database.Task as DatabaseTask

class DatabaseTaskRepository(
    private val database: TodoListDatabase,
) : TaskRepository {

    override suspend fun create(
        name: TaskName,
        description: TaskDescription,
        createdAt: Instant,
        due: Instant,
    ): PlannedTask = database.transactionWithResult {
        database.taskQueries.insertTask(
            name = name.string,
            description = description.string,
            created_at = createdAt.toString(),
            due = due.toString(),
        )

        database.taskQueries.getTaskById(database.taskQueries.getLastInsertedId().executeAsOne())
            .executeAsOne()
            .toScheduledTask()
    }

    override suspend fun getById(id: TaskId): Flow<Task?> {
        return database.taskQueries.getTaskById(id.int.toLong())
            .asFlow()
            .map {
                it.executeAsOneOrNull()
                    ?.toTask()
            }
    }

    override suspend fun update(
        id: TaskId,
        name: TaskName?,
        description: TaskDescription?,
        due: Instant?,
    ): Task? {
        val task = getById(id).first() ?: return null

        database.taskQueries.updateTask(
            id = id.int.toLong(),
            name = name?.string ?: task.name.string,
            description = description?.string ?: task.description.string,
            due = due?.toString() ?: task.due.toString(),
        )
        return getById(id).first()
    }

    override suspend fun delete(id: TaskId): Boolean {
        val task = getById(id).first()
        if (task == null) return false

        database.taskQueries.deleteTask(id.int.toLong())
        return true
    }

    override fun getAll(
        filter: String,
        categories: List<TaskListType>,
    ): Flow<List<Task>> {
        return database.taskQueries.selectAll(
            filter = filter,
            includeScheduled = TaskListType.SCHEDULED in categories || categories.isEmpty(),
            includeInProgress = TaskListType.IN_PROGRESS in categories || categories.isEmpty(),
            includeCompleted = TaskListType.COMPLETED in categories || categories.isEmpty(),
        ).asFlow().map { query -> query.executeAsList().map { it.toTask() } }
    }

    override fun getTasksWithDueBetween(timeRange: ClosedRange<Instant>): Flow<List<Task>> {
        return database.taskQueries.getTasksWithDueBetween(
            start = timeRange.start.toString(),
            end = timeRange.endInclusive.toString(),
        ).asFlow().map { query -> query.executeAsList().map { it.toTask() } }
    }


    override suspend fun getDueTasks(before: Instant): Flow<List<Task>> {
        return database.taskQueries.selectDueTasks(before.toString())
            .asFlow()
            .map { query -> query.executeAsList().map { it.toTask() } }
    }

    override suspend fun getCreatedAfter(timestamp: Instant): List<Task> {
        return database.taskQueries.selectCreatedAfter(timestamp.toString())
            .executeAsList()
            .map { it.toTask() }
    }

    override suspend fun moveToInProgress(
        id: TaskId,
        startedAt: Instant,
    ): InProgressTask? {
        if (getById(id).first() !is PlannedTask) return null

        database.taskQueries.moveToInProgress(
            id = id.int.toLong(),
            started_at = startedAt.toString()
        )

        return database.taskQueries.getTaskById(id.int.toLong())
            .executeAsOne()
            .toInProgressTask()
    }

    override suspend fun moveToCompleted(
        id: TaskId,
        completedAt: Instant,
    ): CompletedTask? {
        if (getById(id).first() !is InProgressTask) return null

        database.taskQueries.moveToCompleted(
            id = id.int.toLong(),
            completed_at = completedAt.toString()
        )

        return database.taskQueries.getTaskById(id.int.toLong())
            .executeAsOne()
            .toCompletedTask()
    }

    private fun DatabaseTask.toTask(): Task {
        return when (status) {
            "scheduled" -> toScheduledTask()
            "in_progress" -> toInProgressTask()
            "completed" -> toCompletedTask()
            else -> throw IllegalArgumentException("Unknown status: $status")
        }
    }

    private fun DatabaseTask.toScheduledTask(): PlannedTask {
        return PlannedTask(
            id = TaskId.factory.createOrThrow(id.toInt()),
            name = TaskName.factory.createOrThrow(name),
            description = TaskDescription.factory.createOrThrow(description),
            createdAt = Instant.parse(created_at),
            due = Instant.parse(due)
        )
    }

    private fun DatabaseTask.toInProgressTask(): InProgressTask {
        return InProgressTask(
            id = TaskId.factory.createOrThrow(id.toInt()),
            name = TaskName.factory.createOrThrow(name),
            description = TaskDescription.factory.createOrThrow(description),
            createdAt = Instant.parse(created_at),
            startedAt = Instant.parse(started_at!!),
            due = Instant.parse(due)
        )
    }

    private fun DatabaseTask.toCompletedTask(): CompletedTask {
        return CompletedTask(
            id = TaskId.factory.createOrThrow(id.toInt()),
            name = TaskName.factory.createOrThrow(name),
            description = TaskDescription.factory.createOrThrow(description),
            createdAt = Instant.parse(created_at),
            startedAt = Instant.parse(started_at!!),
            completedAt = Instant.parse(completed_at ?: error("completed_at should be set for completed task")),
            due = Instant.parse(due),
        )
    }
}
