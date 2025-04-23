package com.y9vad9.todolist.domain.test

import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.GetTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Instant

class GetTaskUseCaseTest {

    private val mockRepo: TaskRepository = mockk()
    private val useCase = GetTaskUseCase(mockRepo)

    private val taskId = TaskId.factory.createOrThrow(42)
    private val task = InProgressTask(
        id = taskId,
        name = TaskName.factory.createOrThrow("Sample Task"),
        description = TaskDescription.factory.createOrThrow("Some description"),
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        startedAt = Instant.parse("2025-01-02T00:00:00Z"),
        due = Instant.parse("2025-01-05T00:00:00Z")
    )

    @Test
    fun `execute should return Success when task is found`() = runTest {
        coEvery { mockRepo.getById(taskId) } returns flowOf(task)

        val result = useCase.execute(taskId).first()

        assertIs<GetTaskUseCase.Result.Success>(result)
        assertEquals(task, result.task)

        @Suppress("UnusedFlow")
        coVerify { mockRepo.getById(taskId) }
    }

    @Test
    fun `execute should return NotFound when task does not exist`() = runTest {
        coEvery { mockRepo.getById(taskId) } returns flowOf(null)

        val result = useCase.execute(taskId).first()

        assertIs<GetTaskUseCase.Result.NotFound>(result)

        @Suppress("UnusedFlow")
        coVerify { mockRepo.getById(taskId) }
    }

    @Test
    fun `execute should return Error when repository throws exception`() = runTest {
        val exception = RuntimeException("Unexpected failure")

        coEvery { mockRepo.getById(taskId) } returns flow {
            throw exception
        }

        val result = useCase.execute(taskId).first()

        assertIs<GetTaskUseCase.Result.Error>(result)
        assertEquals(exception, result.error)

        @Suppress("UnusedFlow")
        coVerify { mockRepo.getById(taskId) }
    }
}
