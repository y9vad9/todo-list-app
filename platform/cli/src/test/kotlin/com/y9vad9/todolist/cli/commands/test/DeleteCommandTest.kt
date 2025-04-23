package com.y9vad9.todolist.cli.commands.test

import com.github.ajalt.clikt.command.test
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.cli.commands.DeleteCommand
import com.y9vad9.todolist.cli.localization.EnglishStrings
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.DeleteTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DeleteCommandTest {

    private val useCase = mockk<DeleteTaskUseCase>()
    private val strings = EnglishStrings

    @Test
    fun `deleteCommand succeeds when task is deleted`() = runTest {
        val taskId = TaskId.factory.createOrThrow(1)
        coEvery { useCase.execute(taskId) } returns DeleteTaskUseCase.Result.Success

        val command = DeleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "1"))

        assertTrue(result.stdout.isEmpty())
    }

    @Test
    fun `deleteCommand fails when task not found`() = runTest {
        val taskId = TaskId.factory.createOrThrow(2)
        coEvery { useCase.execute(taskId) } returns DeleteTaskUseCase.Result.NotFound

        val command = DeleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "2"))

        assertContains(result.stderr, strings.taskNotFoundMessage)
    }

    @Test
    fun `deleteCommand fails when internal error occurs`() = runTest {
        val taskId = TaskId.factory.createOrThrow(3)
        val error = RuntimeException("Unexpected")
        coEvery { useCase.execute(taskId) } returns DeleteTaskUseCase.Result.Error(error)

        val command = DeleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "3"))

        assertContains(result.stderr, strings.internalErrorMessage(error))
    }

    @Test
    fun `deleteCommand fails when ID is negative`() = runTest {
        val command = DeleteCommand(useCase, strings)
        val result = command.test(listOf("--id", "-1"))

        assertContains(result.stderr, strings.idCannotBeNegativeMessage)
    }
}
