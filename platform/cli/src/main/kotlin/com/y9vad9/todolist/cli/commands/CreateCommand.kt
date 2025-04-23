package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.y9vad9.ktiny.kotlidator.createOr
import com.y9vad9.todolist.cli.ext.multilinePromptUntil
import com.y9vad9.todolist.cli.ext.parseToInstant
import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.CreateTaskUseCase
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class CreateCommand(
    private val createTaskUseCase: CreateTaskUseCase,
    private val strings: Strings,
) : SuspendingCliktCommand(name = "create") {
    val taskName: TaskName by option(
        names = arrayOf("--name"),
        help = strings.taskNameOptionDescription,
    ).convert {
        TaskName.factory.createOr(it) {
            fail(strings.taskNameLengthIsInvalid)
        }
    }.prompt(strings.nameTitle)

    val taskDescription: TaskDescription by option(
        names = arrayOf("--description"),
        help = strings.taskDescriptionOptionDescription,
    ).convert {
        TaskDescription.factory.createOr(it) {
            fail(strings.taskDescriptionLengthIsInvalid)
        }
    }.multilinePromptUntil(
        startMessage = strings.promptTaskDescriptionMessage,
        endMarker = ":q",
        default = TaskDescription.EMPTY
    )

    val taskDueDate: Instant by option(
        names = arrayOf("--due"),
        help = strings.taskDueOptionDescription,
    ).convert { input: String ->
        input.parseToInstant() ?: fail(strings.taskDueFormatIsInvalid)
    }.prompt()

    override suspend fun run() {
        val result = createTaskUseCase.execute(
            name = taskName,
            description = taskDescription,
            due = taskDueDate,
        )

        when (result) {
            CreateTaskUseCase.Result.DueInPast -> {
                echo(strings.taskDueCannotBeInPast, err = true)
            }
            is CreateTaskUseCase.Result.Error -> {
                echo(strings.internalErrorMessage(result.error), err = true)
            }
            is CreateTaskUseCase.Result.Success -> {
                echo(strings.taskCreatedMessage(result.task.id))
            }
        }
    }
}