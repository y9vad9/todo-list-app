package com.y9vad9.todolist.cli.commands.test

import com.github.ajalt.clikt.command.test
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.cli.commands.CompleteCommand
import com.y9vad9.todolist.cli.localization.EnglishStrings
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.MoveInProgressToCompletedUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CompleteCommandTest {

    private val useCase = mockk<MoveInProgressToCompletedUseCase>()
    private val strings = EnglishStrings
    private val clock = mockk<Clock>()

    @Test
    fun `completeCommand succeeds when task already completed`() = runTest {
        val taskId = TaskId.factory.createOrThrow(1)

        coEvery {
            useCase.execute(taskId)
        } returns MoveInProgressToCompletedUseCase.Result.AlreadyCompleted

        coEvery {
            clock.now()
        } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = CompleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "1"))

        assert(result.statusCode == 0)
    }

    @Test
    fun `completeCommand succeeds when task successfully moved to completed`() = runTest {
        val taskId = TaskId.factory.createOrThrow(2)

        coEvery {
            useCase.execute(taskId)
        } returns MoveInProgressToCompletedUseCase.Result.Success(mockk())

        coEvery {
            clock.now()
        } returns Instant.parse("2024-01-01T00:00:00Z")

        val command = CompleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "2"))

        assert(result.statusCode == 0)
    }

    @Test
    fun `completeCommand fails when task not found`() = runTest {
        val taskId = TaskId.factory.createOrThrow(3)

        coEvery {
            useCase.execute(taskId)
        } returns MoveInProgressToCompletedUseCase.Result.NotFound

        coEvery {
            clock.now()
        } returns Instant.parse("2024-01-01T00:00:00Z") // Mock the current time

        val command = CompleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "3"))

        assertContains(result.stderr, strings.taskNotFoundMessage)
    }

    @Test
    fun `completeCommand fails when task is not in progress`() = runTest {
        val taskId = TaskId.factory.createOrThrow(4)

        coEvery {
            useCase.execute(taskId)
        } returns MoveInProgressToCompletedUseCase.Result.NotInProgress

        coEvery {
            clock.now()
        } returns Instant.parse("2024-01-01T00:00:00Z") // Mock the current time

        val command = CompleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "4"))

        assertContains(result.stderr, strings.shouldBeStartedFirstMessage)
    }

    @Test
    fun `completeCommand fails when internal error occurs`() = runTest {
        val exception = IllegalStateException("DB Error")

        coEvery {
            useCase.execute(any())
        } returns MoveInProgressToCompletedUseCase.Result.Error(exception)

        coEvery {
            clock.now()
        } returns Instant.parse("2024-01-01T00:00:00Z") // Mock the current time

        val command = CompleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "5"))

        assertContains(result.stderr, strings.internalErrorMessage(exception))
    }

    @Test
    fun `completeCommand fails when id is negative`() = runTest {
        val command = CompleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "-1"))

        assert(strings.idCannotBeNegativeMessage in result.stderr)
        assertEquals(1, result.statusCode)
    }
}
