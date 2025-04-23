package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.UpdateTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.datetime.Instant

class UpdateTaskUseCaseTest {

    private val repo: TaskRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = UpdateTaskUseCase(repo, clock)

    private val id = TaskId.factory.createOrThrow(1)
    private val updatedName = TaskName.factory.createOrThrow("Updated")
    private val updatedDescription = TaskDescription.factory.createOrThrow("Updated description")
    private val updatedDue = Instant.parse("2025-01-10T00:00:00Z")
    private val existingTask: PlannedTask = PlannedTask(
        id = id,
        name = updatedName,
        description = updatedDescription,
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        due = updatedDue
    )

    @Test
    fun `execute returns Success when update is successful`() = runTest {
        coEvery { repo.update(id, updatedName, updatedDescription, updatedDue) } returns existingTask
        every { clock.now() } returns Instant.parse("2025-01-01T00:00:00Z")

        val result = useCase.execute(id, updatedName, updatedDescription, updatedDue)

        assertIs<UpdateTaskUseCase.Result.Success>(result)
        assertEquals(existingTask, result.task)

        coVerify { repo.update(id, updatedName, updatedDescription, updatedDue) }
    }

    @Test
    fun `execute returns NotFound when repository returns null`() = runTest {
        coEvery { repo.update(id, updatedName, updatedDescription, updatedDue) } returns null
        every { clock.now() } returns Instant.parse("2025-01-01T00:00:00Z")

        val result = useCase.execute(id, updatedName, updatedDescription, updatedDue)

        assertIs<UpdateTaskUseCase.Result.NotFound>(result)

        coVerify { repo.update(id, updatedName, updatedDescription, updatedDue) }
    }

    @Test
    fun `execute returns Error when repository throws exception`() = runTest {
        val exception = RuntimeException("Update failure")
        coEvery { repo.update(id, updatedName, updatedDescription, updatedDue) } throws exception
        every { clock.now() } returns Instant.parse("2025-01-01T00:00:00Z")

        val result = useCase.execute(id, updatedName, updatedDescription, updatedDue)

        assertIs<UpdateTaskUseCase.Result.Error>(result)
        assertEquals(exception, result.error)

        coVerify { repo.update(id, updatedName, updatedDescription, updatedDue) }
    }

    @Test
    fun `execute returns DueInPast when due is before now`() = runTest {
        val pastDue = Instant.parse("2020-01-01T00:00:00Z")
        every { clock.now() } returns Instant.parse("2025-01-01T00:00:00Z")

        val result = useCase.execute(id, updatedName, updatedDescription, pastDue)

        assertIs<UpdateTaskUseCase.Result.DueInPast>(result)

        coVerify(exactly = 0) { repo.update(any(), any(), any(), any()) }
    }
}
