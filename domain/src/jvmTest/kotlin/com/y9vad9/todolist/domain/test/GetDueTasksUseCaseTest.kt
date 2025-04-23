package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.GetDueTasksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.datetime.Instant

class GetDueTasksUseCaseTest {

    private val mockRepo: TaskRepository = mockk()
    private val getDueTasksUseCase = GetDueTasksUseCase(mockRepo)

    private val validTaskId = TaskId.factory.createOrThrow(1)
    private val task1 = PlannedTask(
        id = validTaskId,
        name = TaskName.factory.createOrThrow("Task 1"),
        description = TaskDescription.factory.createOrThrow("Description 1"),
        createdAt = Instant.parse("2025-01-01T10:00:00Z"),
        due = Instant.parse("2025-01-02T10:00:00Z")
    )

    private val task2 = PlannedTask(
        id = TaskId.factory.createOrThrow(2),
        name = TaskName.factory.createOrThrow("Task 2"),
        description = TaskDescription.factory.createOrThrow("Description 2"),
        createdAt = Instant.parse("2025-02-01T10:00:00Z"),
        due = Instant.parse("2025-03-01T10:00:00Z")
    )

    @Test
    fun `execute should return tasks due before the given timestamp`() = runTest {
        val timestamp = Instant.parse("2025-02-01T00:00:00Z")

        coEvery { mockRepo.getDueTasks(timestamp) } returns flowOf(listOf(task1))

        val result = getDueTasksUseCase.execute(timestamp)

        assertIs<GetDueTasksUseCase.Result.Success>(result)
        val list = result.tasks.toList().flatten()
        assertEquals(1, list.size)
        assertEquals(task1, list.first())

        coVerify {
            @Suppress("UnusedFlow")
            mockRepo.getDueTasks(timestamp)
        }
    }

    @Test
    fun `execute should return empty list when no tasks are due before the given timestamp`() = runTest {
        val timestamp = Instant.parse("2025-01-01T00:00:00Z")

        coEvery { mockRepo.getDueTasks(before = timestamp) } returns flowOf(emptyList())

        val result = getDueTasksUseCase.execute(timestamp)

        assertIs<GetDueTasksUseCase.Result.Success>(result)
        val list = result.tasks.toList().flatten()
        assertEquals(0, list.size)

        coVerify {
            @Suppress("UnusedFlow")
            mockRepo.getDueTasks(timestamp)
        }
    }

    @Test
    fun `execute should return Error when repository throws an exception`() = runTest {
        val timestamp = Instant.parse("2025-01-01T00:00:00Z")
        val exception = Exception("Database error")

        coEvery { mockRepo.getDueTasks(timestamp) } throws exception

        val result = getDueTasksUseCase.execute(timestamp)

        assertIs<GetDueTasksUseCase.Result.Error>(result)
        assertEquals(exception, result.error)

        coVerify {
            @Suppress("UnusedFlow")
            mockRepo.getDueTasks(timestamp)
        }
    }
}
