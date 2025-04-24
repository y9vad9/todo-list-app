package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.markdown.Markdown
import com.y9vad9.ktiny.kotlidator.createOr
import com.y9vad9.todolist.cli.ext.formatToLocalString
import com.y9vad9.todolist.cli.ext.timeUntilDue
import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.usecase.GetTaskUseCase
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class ViewCommand(
    private val getTaskUseCase: GetTaskUseCase,
    private val strings: Strings,
    private val clock: Clock,
) : SuspendingCliktCommand() {
    private val id: TaskId by argument(
        help = strings.taskIdOptionDescription,
    ).int().convert {
        TaskId.factory.createOr(it) {
            fail(strings.idCannotBeNegativeMessage)
        }
    }

    override suspend fun run() {
        val task = when (val result = getTaskUseCase.execute(id).first()) {
            is GetTaskUseCase.Result.Error -> {
                echo(strings.internalErrorMessage(result.error), err = true)
                return
            }

            GetTaskUseCase.Result.NotFound -> {
                echo(strings.taskNotFoundMessage, err = true)
                return
            }

            is GetTaskUseCase.Result.Success -> result.task
        }

        val markdown = buildString {
            appendLine("# ${task.name.string}")
            appendLine("- ${strings.idTitle}: ${task.id.int}")
            appendLine(
                "- ${strings.dueOrOverdueTitle}: ${
                    task.timeUntilDue(clock.now(), strings)
                }"
            )
            appendLine("- ${strings.createdAtTitle}: ${task.createdAt.formatToLocalString()}")

            when (task) {
                is CompletedTask -> {
                    appendLine("- ${strings.startedAtTitle}: ${task.startedAt.formatToLocalString()}")
                    appendLine("- ${strings.completedAtTitle}: ${task.completedAt.formatToLocalString()}")
                    appendLine("- ${strings.timeSpent}: ${task.timeSpent}")
                }

                is InProgressTask -> {
                    appendLine("- ${strings.startedAtTitle}: ${task.startedAt.formatToLocalString()}")
                }

                is PlannedTask -> {}
            }

            appendLine("_______")
            appendLine(task.description.string.takeIf { it.isNotBlank() }
                ?: "*${strings.noTaskDescriptionProvidedMessage}*")
        }


        echo(Markdown(markdown))
    }
}