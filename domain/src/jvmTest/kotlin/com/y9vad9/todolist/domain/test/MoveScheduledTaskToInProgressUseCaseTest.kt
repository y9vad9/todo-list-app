package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.MoveScheduledTaskToInProgressUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class MoveScheduledTaskToInProgressUseCaseTest {

    private val repo: TaskRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = MoveScheduledTaskToInProgressUseCase(repo, clock)

    private val id = TaskId.factory.createOrThrow(1)
    private val plannedTask = PlannedTask(
        id = id,
        name = TaskName.factory.createOrThrow("Task"),
        description = TaskDescription.factory.createOrThrow("Desc"),
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        due = Instant.parse("2025-01-05T00:00:00Z")
    )

    private val inProgressTask = InProgressTask(
        id = id,
        name = plannedTask.name,
        description = plannedTask.description,
        createdAt = plannedTask.createdAt,
        startedAt = Instant.parse("2025-01-03T00:00:00Z"),
        due = plannedTask.due
    )

    @Test
    fun `execute returns Success when moveToInProgress succeeds`() = runTest {
        val startedAt = Instant.parse("2025-01-03T00:00:00Z")
        coEvery { repo.getById(id) } returns flowOf(plannedTask)
        coEvery { repo.moveToInProgress(id, any()) } returns inProgressTask
        every { clock.now() } returns startedAt.plus(10.minutes)

        val result = useCase.execute(id)

        assertIs<MoveScheduledTaskToInProgressUseCase.Result.Success>(result)
        assertEquals(inProgressTask, result.task)

        coVerify { repo.moveToInProgress(id, any()) }
    }

    @Test
    fun `execute returns AlreadyInProgress when moveToInProgress returns null`() = runTest {
        coEvery { repo.getById(id) } returns flowOf(plannedTask)
        coEvery { repo.moveToInProgress(id, any()) } returns null
        every { clock.now() } returns Instant.parse("2025-01-02T00:00:00Z")

        val result = useCase.execute(id)

        assertIs<MoveScheduledTaskToInProgressUseCase.Result.AlreadyInProgress>(result)

        coVerify { repo.moveToInProgress(id, any()) }
    }

    @Test
    fun `execute returns Error when repository throws exception`() = runTest {
        val exception = RuntimeException("db failure")

        coEvery { repo.getById(id) } returns flowOf(plannedTask)
        every { clock.now() } returns Instant.parse("2025-01-02T00:00:00Z")
        coEvery { repo.moveToInProgress(id, any()) } throws exception

        val result = useCase.execute(id)

        assertIs<MoveScheduledTaskToInProgressUseCase.Result.Error>(result)
        assertEquals(exception, result.error)

        coVerify { repo.moveToInProgress(id, any()) }
    }
}
