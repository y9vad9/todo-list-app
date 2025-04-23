package com.y9vad9.todolist.cli.localization

import com.y9vad9.todolist.domain.type.value.TaskId

interface Strings {
    val listCommand: ListCommand

    interface ListCommand {
        val shouldOnlyPrintDueTasksOptionDescription: String
        val categoryOptionDescription: String
        val optionPageNumberDescription: String
        val filterOptionDescription: String

        fun currentPageAndPagesLeft(currentPage: Int, amount: Int): String
    }

    val editCommand: EditCommand

    interface EditCommand {
        val taskNameOptionDescription: String
        val taskDescriptionOptionDescription: String
        val taskDueOptionDescription: String
        val promptTaskDescriptionMessage: String
    }

    val taskIdOptionDescription: String
    val taskNameOptionDescription: String
    val taskDescriptionOptionDescription: String
    val taskDueOptionDescription: String

    val idTitle: String
    val nameTitle: String
    val dueOrOverdueTitle: String
    val categoryTitle: String
    val createdAtTitle: String

    fun minutes(amount: Int): String
    fun hours(amount: Int): String
    fun days(amount: Int): String
    fun weeks(amount: Int): String
    fun months(amount: Int): String
    fun years(amount: Int): String

    val startedAtTitle: String
    val completedAtTitle: String
    val timeSpent: String

    fun tasksDue(amount: Int): String

    val planedTitle: String
    val inProgressTitle: String
    val completedTitle: String

    fun dueIn(formatted: String): String
    fun dueFor(formatted: String): String

    fun internalErrorMessage(t: Throwable): String

    val taskNotFoundMessage: String

    val idCannotBeNegativeMessage: String
    val taskNameLengthIsInvalid: String
    val taskDescriptionLengthIsInvalid: String
    val taskDueFormatIsInvalid: String
    val taskDueCannotBeInPast: String
    val taskAlreadyStartedMessage: String
    val taskAlreadyCompletedMessage: String

    fun taskCreatedMessage(id: TaskId): String

    val shouldBeStartedFirstMessage: String
    val noTaskDescriptionProvidedMessage: String

    val promptTaskDescriptionMessage: String
}