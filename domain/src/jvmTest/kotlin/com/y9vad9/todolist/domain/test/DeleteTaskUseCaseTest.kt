package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.DeleteTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertIs

class DeleteTaskUseCaseTest {

    private val mockRepo: TaskRepository = mockk()
    private val deleteTaskUseCase = DeleteTaskUseCase(mockRepo)

    private val validTaskId = TaskId.factory.createOrThrow(1)
    private val invalidTaskId = TaskId.factory.createOrThrow(999)

    @Test
    fun `execute should return Success when task is deleted`() = runTest {
        coEvery { mockRepo.delete(validTaskId) } returns true

        val result = deleteTaskUseCase.execute(validTaskId)

        assertIs<DeleteTaskUseCase.Result.Success>(result)

        coVerify { mockRepo.delete(validTaskId) }
    }

    @Test
    fun `execute should return NotFound when task is not found`() = runTest {
        coEvery { mockRepo.delete(invalidTaskId) } returns false

        val result = deleteTaskUseCase.execute(invalidTaskId)

        assertIs<DeleteTaskUseCase.Result.NotFound>(result)

        coVerify { mockRepo.delete(invalidTaskId) }
    }

    @Test
    fun `execute should return Error when repository throws an exception`() = runTest {
        // Arrange: Simulate repository throwing an exception
        val exception = Exception("Something went wrong")
        coEvery { mockRepo.delete(validTaskId) } throws exception

        // Act: Call use case
        val result = deleteTaskUseCase.execute(validTaskId)

        // Assert: Ensure Error result with the correct exception
        assertIs<DeleteTaskUseCase.Result.Error>(result)
        assertTrue { (result as DeleteTaskUseCase.Result.Error).error == exception }

        // Verify repository call
        coVerify { mockRepo.delete(validTaskId) }
    }
}
