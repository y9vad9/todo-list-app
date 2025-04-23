package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.y9vad9.ktiny.kotlidator.createOr
import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.DeleteTaskUseCase

class DeleteCommand(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val strings: Strings,
) : SuspendingCliktCommand() {
    private val id: TaskId by option(
        names = arrayOf("--id"),
        help = strings.taskIdOptionDescription,
    ).int(acceptsValueWithoutName = true).convert {
        TaskId.factory.createOr(it) {
            fail(strings.idCannotBeNegativeMessage)
        }
    }.required()

    override suspend fun run() {
        when (val result = deleteTaskUseCase.execute(id)) {
            is DeleteTaskUseCase.Result.Error -> {
                echo(strings.internalErrorMessage(result.error), err = true)
            }
            DeleteTaskUseCase.Result.NotFound -> {
                echo(strings.taskNotFoundMessage, err = true)
            }
            is DeleteTaskUseCase.Result.Success -> {}
        }
    }
}