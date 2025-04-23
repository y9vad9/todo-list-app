package com.y9vad9.todolist.cli.commands.test

import com.github.ajalt.clikt.command.test
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.cli.commands.ListCommand
import com.y9vad9.todolist.cli.localization.EnglishStrings
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.ListAllTasksUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertContains

class ListCommandTest {

    private val useCase = mockk<ListAllTasksUseCase>()
    private val strings = EnglishStrings
    private val clock = mockk<Clock>()

    @Test
    fun `listCommand succeeds when tasks are listed`() = runTest {
        val task1 = PlannedTask(
            TaskId.factory.createOrThrow(1),
            TaskName.factory.createOrThrow("Task A"),
            TaskDescription.factory.createOrThrow("Description A"),
            Instant.parse("2024-01-01T00:00:00Z"),
            Instant.parse("2025-01-01T00:00:00Z")
        )
        val task2 = InProgressTask(
            TaskId.factory.createOrThrow(2),
            TaskName.factory.createOrThrow("Task B"),
            TaskDescription.factory.createOrThrow("Description B"),
            Instant.parse("2024-01-01T00:00:00Z"),
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2024-02-01T00:00:00Z")
        )

        coEvery {
            useCase.execute(any(), any())
        } returns flowOf(ListAllTasksUseCase.Result.Success(listOf(task1, task2)))

        every {
            clock.now()
        } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = ListCommand(strings, useCase, clock)
        val result = command.test(listOf("--page", "1"))

        assertContains(result.stdout, task1.name.string)
        assertContains(result.stdout, task2.name.string)
    }

    @Test
    fun `listCommand fails when tasks cannot be retrieved`() = runTest {
        val exception = IllegalStateException("DB Error")

        every {
            clock.now()
        } returns Instant.parse("2024-01-01T00:00:00Z")

        coEvery {
            useCase.execute(any(), any())
        } returns flowOf(ListAllTasksUseCase.Result.Error(exception))

        val command = ListCommand(strings, useCase, clock)
        val result = command.test(listOf("--page", "1"))

        assertContains(result.stderr, strings.internalErrorMessage(exception))
    }
}
