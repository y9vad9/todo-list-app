package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.MoveInProgressToCompletedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlinx.datetime.Instant

class MoveInProgressToCompletedUseCaseTest {

    private val repo: TaskRepository = mockk()
    private val useCase = MoveInProgressToCompletedUseCase(repo, Clock.System)

    private val id = TaskId.factory.createOrThrow(1)
    private val inProgress = InProgressTask(
        id = id,
        name = TaskName.factory.createOrThrow("Task"),
        description = TaskDescription.factory.createOrThrow("Desc"),
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        startedAt = Instant.parse("2025-01-02T00:00:00Z"),
        due = Instant.parse("2025-01-05T00:00:00Z")
    )

    private val completed = CompletedTask(
        id = id,
        name = TaskName.factory.createOrThrow("Task"),
        description = TaskDescription.factory.createOrThrow("Desc"),
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        completedAt = Instant.parse("2025-01-06T00:00:00Z"),
        startedAt = Instant.parse("2025-01-04T04:00:00Z"),
        due = Instant.parse("2025-01-05T00:00:00Z")
    )

    private val scheduled = PlannedTask(
        id = id,
        name = TaskName.factory.createOrThrow("Task"),
        description = TaskDescription.factory.createOrThrow("Desc"),
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        due = Instant.parse("2025-01-05T00:00:00Z")
    )

    @Test
    fun `execute returns NotFound when task does not exist`() = runTest {
        coEvery { repo.getById(id) } returns flowOf(null)

        val result = useCase.execute(id)

        assertIs<MoveInProgressToCompletedUseCase.Result.NotFound>(result)
        @Suppress("UnusedFlow")
        coVerify { repo.getById(id) }
    }

    @Test
    fun `execute returns NotInProgress when task is scheduled`() = runTest {
        coEvery { repo.getById(id) } returns flowOf(scheduled)

        val result = useCase.execute(id)

        assertIs<MoveInProgressToCompletedUseCase.Result.NotInProgress>(result)
        @Suppress("UnusedFlow")
        coVerify { repo.getById(id) }
    }

    @Test
    fun `execute returns AlreadyCompleted when task is already completed`() = runTest {
        coEvery { repo.getById(id) } returns flowOf(completed)

        val result = useCase.execute(id)

        assertIs<MoveInProgressToCompletedUseCase.Result.AlreadyCompleted>(result)
        coVerify {
            @Suppress("UnusedFlow")
            repo.getById(id)
        }
    }

    @Test
    fun `execute returns Success when in-progress task is completed successfully`() = runTest {
        coEvery { repo.getById(id) } returns flowOf(inProgress)
        coEvery { repo.moveToCompleted(id, any()) } returns completed

        val result = useCase.execute(id)

        assertIs<MoveInProgressToCompletedUseCase.Result.Success>(result)
        assertEquals(completed, result.task)
        coVerify {
            @Suppress("UnusedFlow")
            repo.getById(id)
            repo.moveToCompleted(id, any())
        }
    }

    @Test
    fun `execute returns NotInProgress when moveToCompleted returns null`() = runTest {
        coEvery { repo.getById(id) } returns flowOf(inProgress)
        coEvery { repo.moveToCompleted(id, any()) } returns null

        val result = useCase.execute(id)

        assertIs<MoveInProgressToCompletedUseCase.Result.NotInProgress>(result)
        coVerify {
            @Suppress("UnusedFlow")
            repo.getById(id)
            repo.moveToCompleted(id, any())
        }
    }

    @Test
    fun `execute returns Error when repository throws exception on getById`() = runTest {
        val exception = RuntimeException("db fail")
        coEvery { repo.getById(id) } throws exception

        val result = useCase.execute(id)

        assertIs<MoveInProgressToCompletedUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
        @Suppress("UnusedFlow")
        coVerify { repo.getById(id) }
    }

    @Test
    fun `execute returns Error when repository throws exception on moveToCompleted`() = runTest {
        val exception = RuntimeException("db fail")
        coEvery { repo.getById(id) } returns flowOf(inProgress)
        coEvery { repo.moveToCompleted(id, any()) } throws exception

        val result = useCase.execute(id)

        assertIs<MoveInProgressToCompletedUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
        coVerify {
            @Suppress("UnusedFlow")
            repo.getById(id)
            repo.moveToCompleted(id, any())
        }
    }
}
