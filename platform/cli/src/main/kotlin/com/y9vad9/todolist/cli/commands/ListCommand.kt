package com.y9vad9.todolist.cli.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.SectionBuilder
import com.github.ajalt.mordant.table.Table
import com.github.ajalt.mordant.table.table
import com.y9vad9.todolist.cli.ext.timeUntilDue
import com.y9vad9.todolist.cli.localization.Strings
import com.y9vad9.todolist.domain.type.*
import com.y9vad9.todolist.domain.usecase.ListAllTasksUseCase
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class ListCommand(
    private val strings: Strings,
    private val listAllTasksUseCase: ListAllTasksUseCase,
    private val clock: Clock,
) : SuspendingCliktCommand("list") {
    companion object {
        const val ITEMS_PER_PAGE = 10
    }

    private val categories by option(
        names = arrayOf("--category"),
        help = strings.listCommand.categoryOptionDescription,
    ).enum<TaskListType>().multiple(required = false)

    private val shouldOnlyPrintDueTasks: Boolean by option(
        names = arrayOf("--due"),
        help = strings.listCommand.shouldOnlyPrintDueTasksOptionDescription,
    ).flag()

    private val filter: String? by option(
        names = arrayOf("--filter"),
        help = strings.listCommand.filterOptionDescription
    )

    private val pageNumber: Int by option(
        names = arrayOf("--page"),
        help = strings.listCommand.optionPageNumberDescription,
    ).int(acceptsValueWithoutName = true).restrictTo(min = 1, max = Int.MAX_VALUE).default(1)

    override suspend fun run() {
        clock.now()

        val tasksList = when (val tasksResult = listAllTasksUseCase.execute(filter.orEmpty(), categories).first()) {
            is ListAllTasksUseCase.Result.Error -> {
                echo(
                    message = strings.internalErrorMessage(tasksResult.error),
                    err = true,
                )
                return
            }

            is ListAllTasksUseCase.Result.Success -> tasksResult.tasks
        }.chunked(ITEMS_PER_PAGE)

        val table = tasksTable(
            tasks = tasksList.getOrNull(pageNumber - 1) ?: emptyList(),
            pagesAmount = tasksList.size,
            currentTime = clock.now(),
        )

        echo(table)

        // fill the remaining space to make effect of 'full-screen output'
        val blankLines = terminal.size.height - table.render(terminal).lines.size

        repeat(blankLines.takeIf { it > 0 } ?: 0) {
            echo("")
        }
    }

    private fun tasksTable(
        tasks: List<Task>,
        pagesAmount: Int,
        currentTime: Instant,
    ): Table {
        val tasksDue = tasks.count { it.isDue(currentTime) }

        return table {
            header {
                style = TextColors.brightBlue + TextStyles.bold

                row(
                    strings.idTitle,
                    strings.nameTitle,
                    strings.dueOrOverdueTitle,
                    strings.categoryTitle,
                    strings.createdAtTitle,
                ) {
                    cellBorders = Borders.NONE
                }
            }

            body {
                style = TextColors.brightBlue

                tasks.forEach { task ->
                    taskRow(task, currentTime)
                }
            }

            footer {
                row("", "", strings.tasksDue(tasksDue), "", "")
                row {
                    cellBorders = Borders.NONE
                    cells("", "", "", "")
                    cell(strings.listCommand.currentPageAndPagesLeft(pageNumber, pagesAmount)) {
                        columnSpan = 2
                        align = TextAlign.CENTER
                    }
                }
            }
        }
    }

    private fun SectionBuilder.taskRow(task: Task, currentTime: Instant) {
        row {
            cell(task.id.int)
            cell(task.name.string)
            cell(currentTime.timeUntilDue(task.due, strings)) {
                val durationUtilDue = task.due - currentTime

                style = when {
                    durationUtilDue < Duration.ZERO -> TextColors.brightRed
                    durationUtilDue < 3.days -> TextColors.brightYellow
                    else -> TextColors.brightGreen
                }
            }
            cell(
                when (task) {
                    is CompletedTask -> strings.completedTitle
                    is InProgressTask -> strings.inProgressTitle
                    is PlannedTask -> strings.planedTitle
                }
            )
            cell(task.createdAt)
        }
    }
}

