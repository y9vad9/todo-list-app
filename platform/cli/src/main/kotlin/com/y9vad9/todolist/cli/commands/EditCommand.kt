package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.y9vad9.ktiny.kotlidator.createOr
import com.y9vad9.ktiny.kotlidator.createOrNull
import com.y9vad9.todolist.cli.ext.multilinePromptUntil
import com.y9vad9.todolist.cli.ext.parseToInstant
import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.domain.usecase.UpdateTaskUseCase

class EditCommand(
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val strings: Strings,
) : SuspendingCliktCommand(name = "edit") {
    private val id: TaskId by argument(
        help = strings.taskIdOptionDescription,
    ).int().convert {
        TaskId.factory.createOr(it) {
            fail(strings.idCannotBeNegativeMessage)
        }
    }

    val taskName: String by option(
        names = arrayOf("--name"),
        help = strings.editCommand.taskNameOptionDescription,
    ).prompt(strings.editCommand.taskNameOptionDescription)

    val taskDescription: String by option(
        names = arrayOf("--description"),
        help = strings.editCommand.taskDescriptionOptionDescription,
    ).multilinePromptUntil(
        startMessage = strings.editCommand.taskDescriptionOptionDescription,
        endMarker = ":q",
        default = ""
    )

    val taskDueDate: String by option(
        names = arrayOf("--due"),
        help = strings.taskDueOptionDescription,
    ).prompt(strings.editCommand.taskDueOptionDescription)

    override suspend fun run() {
        val name = taskName.takeIf { it.isNotBlank() }
            ?.let {
                TaskName.factory.createOrNull(it) ?: run {
                    echo(strings.taskNameLengthIsInvalid, err = true)
                    return
                }
            }
        val description = taskDescription.takeIf { it.isNotBlank() }
            ?.let {
                TaskDescription.factory.createOrNull(it) ?: run {
                    echo(strings.taskDescriptionLengthIsInvalid, err = true)
                    return
                }
            }
        val due = taskDueDate.takeIf { it.isNotBlank() }
            ?.let {
                it.parseToInstant() ?: run {
                    echo(strings.taskDueFormatIsInvalid, err = true)
                    return
                }
            }

        when (val result = updateTaskUseCase.execute(id, name, description, due)) {
            UpdateTaskUseCase.Result.DueInPast -> {
                echo(strings.taskDueCannotBeInPast, err = true)
            }
            is UpdateTaskUseCase.Result.Error -> {
                echo(strings.internalErrorMessage(result.error), err = true)
            }
            UpdateTaskUseCase.Result.NotFound -> {
                echo(strings.taskNotFoundMessage, err = true)
            }
            is UpdateTaskUseCase.Result.Success -> {}
        }
    }
}