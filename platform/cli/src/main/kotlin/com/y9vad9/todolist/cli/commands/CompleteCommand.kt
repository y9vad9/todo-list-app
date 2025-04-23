package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.y9vad9.ktiny.kotlidator.createOr
import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.MoveInProgressToCompletedUseCase

class CompleteCommand(
    private val moveInProgressToCompletedUseCase: MoveInProgressToCompletedUseCase,
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
        when (val result = moveInProgressToCompletedUseCase.execute(id)) {
            is MoveInProgressToCompletedUseCase.Result.Error -> {
                echo(strings.internalErrorMessage(result.error), err = true)
            }

            MoveInProgressToCompletedUseCase.Result.NotFound -> {
                echo(strings.taskNotFoundMessage, err = true)
            }

            MoveInProgressToCompletedUseCase.Result.NotInProgress -> {
                echo(strings.shouldBeStartedFirstMessage, err = true)
            }

            // valid, no need to print anything
            MoveInProgressToCompletedUseCase.Result.AlreadyCompleted -> {
                echo(strings.taskAlreadyCompletedMessage, err = true)
            }

            is MoveInProgressToCompletedUseCase.Result.Success -> {}
        }
    }
}