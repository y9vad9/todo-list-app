package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import com.y9vad9.ktiny.kotlidator.createOr
import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.MoveScheduledTaskToInProgressUseCase
import kotlinx.datetime.Clock

class StartCommand(
    private val moveScheduledTaskToInProgressUseCase: MoveScheduledTaskToInProgressUseCase,
    private val strings: Strings,
) : SuspendingCliktCommand() {
    private val id: TaskId by option(
        names = arrayOf("--id"),
        help = strings.taskIdOptionDescription,
    ).int(acceptsValueWithoutName = true).restrictTo(1..Int.MAX_VALUE).convert {
        TaskId.factory.createOr(it) {
            fail(strings.idCannotBeNegativeMessage)
        }
    }.required()

    override suspend fun run() {
        when (val result = moveScheduledTaskToInProgressUseCase.execute(id)) {
            is MoveScheduledTaskToInProgressUseCase.Result.Error -> {
                echo(strings.internalErrorMessage(result.error), err = true)
            }

            MoveScheduledTaskToInProgressUseCase.Result.NotFound -> {
                echo(strings.taskNotFoundMessage, err = true)
            }

            MoveScheduledTaskToInProgressUseCase.Result.AlreadyInProgress -> {
                echo(strings.taskAlreadyStartedMessage, err = true)
            }

            MoveScheduledTaskToInProgressUseCase.Result.AlreadyCompleted -> {
                echo(strings.taskAlreadyCompletedMessage, err = true)
            }

            is MoveScheduledTaskToInProgressUseCase.Result.Success -> {}
        }
    }
}