package com.y9vad9.todolist.cli.localization

import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName

object EnglishStrings : Strings {
    override val listCommand: Strings.ListCommand = object : Strings.ListCommand {
        override val shouldOnlyPrintDueTasksOptionDescription: String =
            "Specifies whether only overdue tasks should be printed."

        override val categoryOptionDescription: String =
            "List of task types to print. By default, all tasks are printed."

        override val optionPageNumberDescription: String =
            "Specifies the page number."

        override val filterOptionDescription: String = "Filter tasks by name/description."

        override fun currentPageAndPagesLeft(currentPage: Int, amount: Int): String {
            return "Page $currentPage of $amount total."
        }
    }

    override val editCommand: Strings.EditCommand = object : Strings.EditCommand {
        override val taskNameOptionDescription: String = "New task name (or empty to leave unchanged)"
        override val taskDescriptionOptionDescription: String = "New task description (or empty to leave unchanged)"
        override val taskDueOptionDescription: String = "New due date for task (or empty to leave unchanged)"
        override val promptTaskDescriptionMessage: String =
            "Enter a task description (multiline; write :q to exit; leave empty to leave unchanged):"
    }

    override val taskIdOptionDescription: String =
        "The ID of the task being referenced. Cannot be negative."

    override val taskNameOptionDescription: String =
        "The name of the task. Length must be within ${TaskName.LENGTH_RANGE}."

    override val taskDescriptionOptionDescription: String =
        "The description of the task. Supports Markdown. Maximum 10,000 characters."

    override val taskDueOptionDescription: String =
        "Specifies the due date and time of the task."

    override val idTitle: String = "ID"
    override val nameTitle: String = "Name"
    override val dueOrOverdueTitle: String = "Due / Overdue"
    override val categoryTitle: String = "Category"
    override val createdAtTitle: String = "Created At"
    override fun seconds(amount: Int): String {
        return "$amount seconds"
    }

    override fun minutes(amount: Int): String =
        if (amount == 1) "$amount minute" else "$amount minutes"

    override fun hours(amount: Int): String =
        if (amount == 1) "$amount hour" else "$amount hours"

    override fun days(amount: Int): String =
        if (amount == 1) "$amount day" else "$amount days"

    override fun weeks(amount: Int): String =
        if (amount == 1) "$amount week" else "$amount weeks"

    override fun months(amount: Int): String =
        if (amount == 1) "$amount month" else "$amount months"

    override fun years(amount: Int): String =
        if (amount == 1) "$amount year" else "$amount years"

    override val startedAtTitle: String = "Started At"
    override val completedAtTitle: String = "Completed At"
    override val timeSpent: String = "Time Spent"

    override fun tasksDue(amount: Int): String =
        "$amount tasks due"

    override val planedTitle: String = "Planned"
    override val inProgressTitle: String = "In Progress"
    override val completedTitle: String = "Completed"

    override fun dueIn(formatted: String): String =
        "$formatted remaining"

    override fun dueFor(formatted: String): String =
        "$formatted overdue"

    override fun wasDueFor(formatted: String): String {
        return "Was due for $formatted"
    }

    override fun wasCompletedEarlier(formatted: String): String {
        return "Done earlier by $formatted"
    }

    override fun internalErrorMessage(t: Throwable): String =
        "An internal error occurred: ${t.message}"

    override val taskNotFoundMessage: String =
        "Task with the specified ID was not found."

    override val idCannotBeNegativeMessage: String =
        "ID cannot be negative."

    override val taskNameLengthIsInvalid: String =
        "Task name length is invalid. It must be between ${TaskName.LENGTH_RANGE} characters."

    override val taskDescriptionLengthIsInvalid: String =
        "Task description length is invalid. It must be between ${TaskDescription.LENGTH_RANGE} characters."

    override val taskDueFormatIsInvalid: String =
        "Invalid due date format. Accepted formats: yyyy-MM-dd HH:mm, yyyy-MM-dd, yyyy/MM/dd HH:mm, MMM d, yyyy, MMMM d, yyyy."

    override val taskDueCannotBeInPast: String =
        "The task's due date/time cannot be in the past."
    override val taskAlreadyStartedMessage: String = "The task is already started."
    override val taskAlreadyCompletedMessage: String = "The task is already completed."

    override fun taskCreatedMessage(id: TaskId): String =
        "Task successfully created with ID: ${id.int}."

    override val shouldBeStartedFirstMessage: String =
        "The task cannot be completed before it has been started."

    override val noTaskDescriptionProvidedMessage: String =
        "No description provided for this task."

    override val promptTaskDescriptionMessage: String =
        "Enter a task description (optional, multiline; write :q to exit):"
}