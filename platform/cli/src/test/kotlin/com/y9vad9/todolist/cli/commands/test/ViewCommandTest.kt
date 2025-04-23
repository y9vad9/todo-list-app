package com.y9vad9.todolist.cli.commands.test

import com.github.ajalt.clikt.command.test
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.cli.commands.ViewCommand
import com.y9vad9.todolist.cli.localization.EnglishStrings
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.GetTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ViewCommandTest {

    private val useCase = mockk<GetTaskUseCase>()
    private val strings = EnglishStrings
    private val clock = mockk<Clock>()

    @Test
    fun `viewCommand succeeds when task is found and displayed`() = runTest {
        val taskId = TaskId.factory.createOrThrow(1)
        val name = TaskName.factory.createOrThrow("Task A")
        val description = TaskDescription.factory.createOrThrow("This is a test task description.")
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val due = Instant.parse("2025-01-01T00:00:00Z")
        val task = PlannedTask(taskId, name, description, createdAt, due)

        coEvery { useCase.execute(taskId) } returns flowOf(GetTaskUseCase.Result.Success(task))
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = ViewCommand(useCase, strings, clock)
        val result = command.test(listOf("--id", "1"))

        assertContains(result.stdout, task.name.string)
        assertContains(result.stdout, task.description.string)
    }

    @Test
    fun `viewCommand fails when task not found`() = runTest {
        val taskId = TaskId.factory.createOrThrow(2)

        coEvery { useCase.execute(taskId) } returns flowOf(GetTaskUseCase.Result.NotFound)

        val command = ViewCommand(useCase, strings, clock)
        val result = command.test(listOf("--id", "2"))

        assertContains(result.stderr, strings.taskNotFoundMessage)
    }

    @Test
    fun `viewCommand fails when internal error occurs`() = runTest {
        val taskId = TaskId.factory.createOrThrow(3)
        val exception = IllegalStateException("DB Error")

        coEvery { useCase.execute(taskId) } returns flowOf(GetTaskUseCase.Result.Error(exception))

        val command = ViewCommand(useCase, strings, clock)
        val result = command.test(listOf("--id", "3"))

        assertContains(result.stderr, strings.internalErrorMessage(exception))
    }
}
