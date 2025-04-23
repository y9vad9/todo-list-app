package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.GetCreatedAfterUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.datetime.Instant

class GetCreatedAfterUseCaseTest {

    private val mockRepo: TaskRepository = mockk()
    private val getCreatedAfterUseCase = GetCreatedAfterUseCase(mockRepo)

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
        createdAt = Instant.parse("2025-03-01T10:00:00Z"),
        due = Instant.parse("2025-03-02T10:00:00Z"),
    )

    @Test
    fun `execute should return tasks created after the given timestamp`() = runTest {
        val timestamp = Instant.parse("2025-02-01T00:00:00Z")

        coEvery { mockRepo.getCreatedAfter(timestamp) } returns listOf(task2)

        val result = getCreatedAfterUseCase.execute(timestamp)

        assertIs<GetCreatedAfterUseCase.Result.Success>(result)
        assertEquals(1, result.tasks.size)
        assertEquals(task2, result.tasks.first())

        coVerify { mockRepo.getCreatedAfter(timestamp) }
    }

    @Test
    fun `execute should return empty list when no tasks are created after the given timestamp`() = runTest {
        val timestamp = Instant.parse("2025-04-01T00:00:00Z")

        coEvery { mockRepo.getCreatedAfter(timestamp) } returns emptyList()

        val result = getCreatedAfterUseCase.execute(timestamp)

        assertIs<GetCreatedAfterUseCase.Result.Success>(result)
        assertEquals(0, result.tasks.size)

        coVerify { mockRepo.getCreatedAfter(timestamp) }
    }

    @Test
    fun `execute should return Error when repository throws an exception`() = runTest {
        val timestamp = Instant.parse("2025-01-01T00:00:00Z")
        val exception = Exception("Database error")

        coEvery { mockRepo.getCreatedAfter(timestamp) } throws exception

        val result = getCreatedAfterUseCase.execute(timestamp)

        assertIs<GetCreatedAfterUseCase.Result.Error>(result)
        assertEquals(exception, result.error)

        coVerify { mockRepo.getCreatedAfter(timestamp) }
    }
}
