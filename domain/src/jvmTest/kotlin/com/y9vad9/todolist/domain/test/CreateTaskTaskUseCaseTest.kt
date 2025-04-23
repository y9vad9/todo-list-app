package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.CreateTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlinx.datetime.Instant
import kotlin.test.assertIs

class CreateTaskTaskUseCaseTest {
    private val repo = mockk<TaskRepository>()
    private val clock: Clock = mockk()
    private val useCase = CreateTaskUseCase(repo, clock)

    private val name = TaskName.factory.createOrThrow("Write unit tests")
    private val description = TaskDescription.factory.createOrThrow("Cover use cases with mockk")
    private val createdAt = Instant.parse("2024-01-01T12:00:00Z")
    private val due = Instant.parse("2024-01-03T12:00:00Z")

    private val expectedTask = PlannedTask(
        id = TaskId.factory.createOrThrow(1),
        name = name,
        description = description,
        due = due,
        createdAt = createdAt
    )

    @Test
    fun `should return Success when task is created successfully`() = runTest {
        every { clock.now() } returns createdAt
        coEvery { repo.create(name, description, createdAt, due) } returns expectedTask

        val result = useCase.execute(name, description, due)

        when (result) {
            is CreateTaskUseCase.Result.Success -> assertEquals(expectedTask, result.task)
            else -> fail("Expected Success but got $result")
        }

        coVerify(exactly = 1) { repo.create(name, description, createdAt, due) }
    }

    @Test
    fun `should return Error when repository throws`() = runTest {
        every { clock.now() } returns createdAt
        val exception = RuntimeException("DB failure")
        coEvery { repo.create(name, description, createdAt, due) } throws exception

        val result = useCase.execute(name, description, due)

        when (result) {
            is CreateTaskUseCase.Result.Error -> assertEquals(exception, result.error)
            else -> fail("Expected Error but got $result")
        }

        coVerify(exactly = 1) { repo.create(name, description, createdAt, due) }
    }

    @Test
    fun `should return DueInPast when due is in the past`() = runTest {
        val now = Instant.parse("2025-01-01T00:00:00Z")
        val pastDue = Instant.parse("2024-12-31T23:59:59Z")
        every { clock.now() } returns now

        val result = useCase.execute(name, description, pastDue)

        assertIs<CreateTaskUseCase.Result.DueInPast>(result)

        coVerify(exactly = 0) { repo.create(any(), any(), any(), any()) }
    }
}

