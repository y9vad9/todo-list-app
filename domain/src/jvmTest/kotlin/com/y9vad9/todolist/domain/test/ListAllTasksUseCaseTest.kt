package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.ListAllTasksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlinx.datetime.Instant

class ListAllTasksUseCaseTest {

    private val mockRepo: TaskRepository = mockk()
    private val useCase = ListAllTasksUseCase(mockRepo)

    private val task1 = PlannedTask(
        id = TaskId.factory.createOrThrow(1),
        name = TaskName.factory.createOrThrow("First task"),
        description = TaskDescription.factory.createOrThrow("Desc 1"),
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        due = Instant.parse("2025-01-02T00:00:00Z")
    )

    private val task2 = CompletedTask(
        id = TaskId.factory.createOrThrow(2),
        name = TaskName.factory.createOrThrow("Second task"),
        description = TaskDescription.factory.createOrThrow("Desc 2"),
        createdAt = Instant.parse("2025-01-03T00:00:00Z"),
        startedAt = Instant.parse("2025-01-04T04:00:00Z"),
        completedAt = Instant.parse("2025-01-04T00:00:00Z"),
        due = Instant.parse("2025-01-05T00:00:00Z")
    )

    @Test
    fun `execute should return Success with all tasks`() = runTest {
        coEvery { mockRepo.getAll(any(), any()) } returns flowOf(listOf(task1, task2))

        val result = useCase.execute("", emptyList())
            .first()

        assertIs<ListAllTasksUseCase.Result.Success>(result)
        assertEquals(listOf(task1, task2), result.tasks)

        @Suppress("UnusedFlow")
        coVerify { mockRepo.getAll() }
    }

    @Test
    fun `execute should return Success with empty list when there are no tasks`() = runTest {
        coEvery { mockRepo.getAll(any(), any()) } returns flowOf(emptyList())

        val result = useCase.execute("", emptyList())
            .first()

        assertIs<ListAllTasksUseCase.Result.Success>(result)
        assertEquals(emptyList(), result.tasks)

        @Suppress("UnusedFlow")
        coVerify { mockRepo.getAll() }
    }

    @Test
    fun `execute should return Error when repository throws exception`() = runTest {
        val exception = RuntimeException("Unexpected error")

        coEvery { mockRepo.getAll(any(), any()) } returns flow {
            throw exception
        }

        val result = useCase.execute("", emptyList())
            .first()

        assertIs<ListAllTasksUseCase.Result.Error>(result)
        assertEquals(exception, result.error)

        @Suppress("UnusedFlow")
        coVerify { mockRepo.getAll() }
    }
}
