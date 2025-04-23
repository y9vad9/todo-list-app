package com.y9vad9.todolist.cli.commands.test

import com.github.ajalt.clikt.command.test
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.cli.commands.CreateCommand
import com.y9vad9.todolist.cli.localization.EnglishStrings
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.CreateTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CreateCommandTest {
    private val useCase = mockk<CreateTaskUseCase>()
    private val strings = EnglishStrings
    private val clock = mockk<Clock>()

    @Test
    fun `createCommand succeeds when task created`() = runTest {
        val name = TaskName.factory.createOrThrow("Task A")
        val description = TaskDescription.factory.createOrThrow("Some description")
        val due = Instant.parse("2025-01-01T00:00:00Z")
        val task = PlannedTask(TaskId.factory.createOrThrow(1), name, description, Instant.parse("2024-01-01T00:00:00Z"), due)

        coEvery { useCase.execute(name, description, any()) } returns CreateTaskUseCase.Result.Success(task)
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = CreateCommand(useCase, strings)
        val result = command.test(
            listOf("--name", "Task A", "--description", "Some description", "--due", "2025-01-01")
        )

        assertContains(result.stdout, strings.taskCreatedMessage(task.id))
        assertEquals(0, result.statusCode)
    }

    @Test
    fun `createCommand fails when due date in past`() = runTest {
        val name = TaskName.factory.createOrThrow("Task B")
        val description = TaskDescription.factory.createOrThrow("Description B")

        coEvery { useCase.execute(name, description, any()) } returns CreateTaskUseCase.Result.DueInPast
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = CreateCommand(useCase, strings)
        val result = command.test(listOf("--name", "Task B", "--description", "Description B", "--due", "2023-01-01"))

        assertContains(result.stderr, strings.taskDueCannotBeInPast)
        assertEquals(0, result.statusCode)
    }

    @Test
    fun `createCommand fails when internal error occurs`() = runTest {
        val name = TaskName.factory.createOrThrow("Task C")
        val description = TaskDescription.factory.createOrThrow("Description C")
        val exception = IllegalStateException("DB Error")

        coEvery { useCase.execute(name, description, any()) } returns CreateTaskUseCase.Result.Error(exception)
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = CreateCommand(useCase, strings)
        val result = command.test(listOf("--name", "Task C", "--description", "Description C", "--due", "2025-01-01"))

        assertContains(result.stderr, strings.internalErrorMessage(exception))
        assertEquals(0, result.statusCode)
    }

    @Test
    fun `createCommand fails when name too short`() = runTest {
        val command = CreateCommand(useCase, strings)
        val result = command.test(listOf("--name", "", "--description", "Valid", "--due", "2025-01-01"))

        assertContains(result.stderr, strings.taskNameLengthIsInvalid)
        assertNotEquals(0, result.statusCode)
    }

    @Test
    fun `createCommand fails when due date invalid`() = runTest {
        val command = CreateCommand(useCase, strings)
        val result = command.test(listOf("--name", "Valid", "--description", "Valid", "--due", "not-a-date"))

        assertContains(result.stderr, strings.taskDueFormatIsInvalid)
        assertNotEquals(0, result.statusCode)
    }
}
