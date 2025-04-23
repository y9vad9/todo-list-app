package com.y9vad9.todolist.database.test

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.database.TodoListDatabase
import com.y9vad9.todolist.domain.type.TaskListType
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.integration.DatabaseTaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.random.Random
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

class DatabaseTaskRepositoryTest {
    private lateinit var repository: DatabaseTaskRepository
    private lateinit var database: TodoListDatabase
    private lateinit var tempFile: File

    @Before
    fun setup(): Unit = runTest {
        tempFile = createTempFile("test-db", ".sqlite").toFile()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${tempFile.absolutePath}")
        TodoListDatabase.Schema.create(driver).await()
        database = TodoListDatabase(
            driver
        )
        repository = DatabaseTaskRepository(database)
    }

    @After
    fun cleanup() {
        if (::tempFile.isInitialized) tempFile.delete()
    }

    private fun generateTaskName() = TaskName.factory.createOrThrow("My Test Task ${Random.nextInt()}")
    private fun generateTaskDescription() = TaskDescription.factory.createOrThrow("A".repeat(50))
    private fun now() = Instant.fromEpochMilliseconds(System.currentTimeMillis())
    private fun later(seconds: Long = 60) = now().plus(seconds.seconds)

    @Test
    fun `create and get task by id`() = runTest {
        val created = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        val fetched = repository.getById(created.id).first()
        assertEquals(created, fetched)
    }

    @Test
    fun `update task`() = runTest {
        val created = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        val updatedName = TaskName.factory.createOrThrow("Updated Task Name")
        val updatedDescription = TaskDescription.factory.createOrThrow("Updated Description")
        val updated = repository.update(created.id, updatedName, updatedDescription, null)
        assertNotNull(updated)
        assertEquals(updatedName, updated.name)
        assertEquals(updatedDescription, updated.description)
    }

    @Test
    fun `delete task`() = runTest {
        val created = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        assertTrue(repository.delete(created.id))
        assertNull(repository.getById(created.id).first())
    }

    @Test
    fun `get all tasks with empty filter`() = runTest {
        val t1 = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        val t2 = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        val tasks = repository.getAll("", TaskListType.entries).first()
        assertTrue(tasks.any { it.id == t1.id })
        assertTrue(tasks.any { it.id == t2.id })
    }

    @Test
    fun `get tasks with due between`() = runTest {
        val now = now()

        // t1: due BEFORE the range
        val t1 = repository.create(
            generateTaskName(),
            generateTaskDescription(),
            now,
            now.plus(10.seconds)
        )

        // t2: due INSIDE the range
        val t2 = repository.create(
            generateTaskName(),
            generateTaskDescription(),
            now,
            now.plus(60.seconds)
        )

        // t3: due AFTER the range
        val t3 = repository.create(
            generateTaskName(),
            generateTaskDescription(),
            now,
            now.plus(600.seconds)
        )

        val rangeStart = now.plus(30.seconds)
        val rangeEnd = now.plus(300.seconds)

        val tasks = repository.getTasksWithDueBetween(rangeStart..rangeEnd).first()

        // t2 is the only one due in the range
        assertTrue(tasks.any { it.id == t2.id })
        assertFalse(tasks.any { it.id == t1.id })
        assertFalse(tasks.any { it.id == t3.id })
    }


    @Test
    fun `get due tasks`() = runTest {
        val now = now()
        val dueSoon = repository.create(generateTaskName(), generateTaskDescription(), now, now.plus(10.seconds))
        repository.create(generateTaskName(), generateTaskDescription(), now, now.plus(600.seconds))
        val due = repository.getDueTasks(now.plus(30.seconds)).first()
        assertEquals(1, due.size)
        assertEquals(dueSoon.id, due[0].id)
    }

    @Test
    fun `get created after`() = runTest {
        val before = now()
        val old = repository.create(
            generateTaskName(),
            generateTaskDescription(),
            createdAt = before,
            due = later()
        )

        val after = before.plus(5.seconds)

        val recent = repository.create(
            generateTaskName(),
            generateTaskDescription(),
            createdAt = after.plus(1.seconds),
            due = later()
        )

        val result = repository.getCreatedAfter(after)

        assertTrue(result.any { it.id == recent.id })
        assertFalse(result.any { it.id == old.id })
    }


    @Test
    fun `move to in-progress and fail when repeated`() = runTest {
        val created = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        val inProgress = repository.moveToInProgress(created.id, now())
        assertNotNull(inProgress)
        val second = repository.moveToInProgress(created.id, now())
        assertNull(second)
    }

    @Test
    fun `move to completed only from scheduled fails`() = runTest {
        val created = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        val completed = repository.moveToCompleted(created.id, now())
        assertNull(completed)
    }

    @Test
    fun `move from in-progress to completed with correct duration`() = runTest {
        val scheduled = repository.create(generateTaskName(), generateTaskDescription(), now(), later())
        val started = now()
        val completedAt = started.plus(30.seconds)
        val inProgress = repository.moveToInProgress(scheduled.id, started)!!
        val completed = repository.moveToCompleted(inProgress.id, completedAt)!!
        assertEquals(30.seconds.inWholeMilliseconds, completed.timeSpent.inWholeMilliseconds)
    }
}
