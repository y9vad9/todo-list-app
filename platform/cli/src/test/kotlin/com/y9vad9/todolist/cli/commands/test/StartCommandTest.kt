package com.y9vad9.todolist.cli.commands.test

import com.github.ajalt.clikt.command.test
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.cli.commands.StartCommand
import com.y9vad9.todolist.cli.localization.EnglishStrings
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.MoveScheduledTaskToInProgressUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StartCommandTest {

    private val useCase = mockk<MoveScheduledTaskToInProgressUseCase>()
    private val strings = EnglishStrings
    private val clock = mockk<Clock>()

    @Test
    fun `startCommand succeeds when task successfully moved to in-progress`() = runTest {
        val taskId = TaskId.factory.createOrThrow(1)

        coEvery { useCase.execute(taskId) } returns MoveScheduledTaskToInProgressUseCase.Result.Success(mockk())
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = StartCommand(useCase, strings)
        val result = command.test(listOf("--id", "1"))

        assertEquals("", result.stderr)
    }

    @Test
    fun `startCommand fails when task already in progress`() = runTest {
        val taskId = TaskId.factory.createOrThrow(2)

        coEvery { useCase.execute(taskId) } returns MoveScheduledTaskToInProgressUseCase.Result.AlreadyInProgress
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = StartCommand(useCase, strings)
        val result = command.test(listOf("--id", "2"))

        assertContains(result.stderr, strings.taskAlreadyStartedMessage)
    }

    @Test
    fun `startCommand fails when task already completed`() = runTest {
        val taskId = TaskId.factory.createOrThrow(3)

        coEvery { useCase.execute(taskId) } returns MoveScheduledTaskToInProgressUseCase.Result.AlreadyCompleted
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = StartCommand(useCase, strings)
        val result = command.test(listOf("--id", "3"))

        assertContains(result.stderr, strings.taskAlreadyCompletedMessage)
    }

    @Test
    fun `startCommand fails when task not found`() = runTest {
        val taskId = TaskId.factory.createOrThrow(4)

        coEvery { useCase.execute(taskId) } returns MoveScheduledTaskToInProgressUseCase.Result.NotFound
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = StartCommand(useCase, strings)
        val result = command.test(listOf("--id", "4"))

        assertContains(result.stderr, strings.taskNotFoundMessage)
    }

    @Test
    fun `startCommand fails when internal error occurs`() = runTest {
        val taskId = TaskId.factory.createOrThrow(5)
        val exception = IllegalStateException("DB Error")

        coEvery { useCase.execute(taskId) } returns MoveScheduledTaskToInProgressUseCase.Result.Error(exception)
        coEvery { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = StartCommand(useCase, strings)
        val result = command.test(listOf("--id", "5"))

        assertContains(result.stderr, strings.internalErrorMessage(exception))
    }
}

