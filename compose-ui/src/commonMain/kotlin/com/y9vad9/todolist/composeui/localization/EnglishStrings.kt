package com.y9vad9.todolist.composeui.localization

object EnglishStrings : Strings {
    override val appName = "TodoList"

    // -- Create Task Screen
    override val createTaskTitle = "Create Task"
    override val taskNameTitle = "Task Name"
    override val taskDescriptionTitle = "Description"
    override val taskDateTitle = "Due Date"
    override val taskTimeOfTheDayTitle: String = "Due time of the day"
    override val createTaskButton = "Create"

    // -- Edit Task Screen --
    override val editTaskTitle = "Edit Task"
    override val deleteTaskButton = "Delete"
    override val editTaskButton = "Save Changes"

    // -- View Task Screen --
    override val viewTaskTitle = "Task Details"
    override val viewTaskButton = "View"
    override val editTaskButtonDescription = "Edit"
    override val dueToTitle = "Due to"
    override val createdAtTitle = "Created at"
    override val statusTitle = "Status"
    override val startedAtTitle = "Started at"
    override val finishedAtTitle = "Completed at"
    override val markAsInProgressButton: String = "Mark as In Progress"
    override val markAsCompletedButton: String = "Mark as Completed"
    override val completedAtTitle: String = "Completed at"

    // -- Important Screen --
    override val overdueTasksTitle = "Overdue Tasks"
    override val tasksDueTodayTitle: String = "Due Today"
    override val tasksUntilTomorrowTitle = "Due by Tomorrow"
    override val tasksThisWeekTitle = "Due This Week"
    override val tasksNextWeekTitle = "Due Next Week"
    override val importantTitle = "Important"

    // -- Settings Screen --
    override val appLanguageTitle = "Language"
    override val appThemeTitle = "Theme"
    override val currentLanguageTitle = "English"
    override val lightThemeTitle = "Light"
    override val darkThemeTitle = "Dark"
    override val systemThemeTitle = "System"
    override val settingsTitle = "Settings"

    // -- Task Categories --
    override val completedTitle = "Completed"
    override val inProgressTitle = "In Progress"
    override val planedTitle = "Planned"

    // -- Relative Time Helpers --
    override fun overdueBy(value: String) = "$value overdue"
    override fun dueIn(value: String) = "Due in $value"
    override fun completedEarlyBy(value: String): String {
        return "Completed early by $value"
    }

    override fun seconds(value: Int) = if (value == 1) "1 second" else "$value seconds"
    override fun minutes(value: Int) = if (value == 1) "1 minute" else "$value minutes"
    override fun hours(value: Int) = if (value == 1) "1 hour" else "$value hours"
    override fun days(value: Int) = if (value == 1) "1 day" else "$value days"
    override fun weeks(value: Int) = if (value == 1) "1 week" else "$value weeks"
    override fun months(value: Int) = if (value == 1) "1 month" else "$value months"
    override fun years(value: Int) = if (value == 1) "1 year" else "$value years"

    // -- Error & Feedback --
    override fun internalErrorMessage(t: Throwable) = "An internal error occurred: ${t.message ?: "unknown error"}"
    override val failureMessage = "Operation failed. Please try again."
    override val noItemsInImportantYetMessage: String = "No items appear to be in important. We show here upcoming tasks to be due."

    // -- Validation & Actions --
    override val dateCannotBeInPastMessage = "The date cannot be in the past."
    override val goBackActionDescription = "Go back"

    // -- All Tasks Screen --
    override val allTasksTitle = "All Tasks"
    override val filterTitle: String = "Filter"

    // -- Validation --
    override fun maxNumberValueFailure(max: Number): String {
        return "The value cannot be greater than $max."
    }

    override fun minNumberValueFailure(min: Number): String {
        return "The value cannot be less than $min."
    }

    override fun numberRangeFailure(min: Number, max: Number): String {
        return "The value must be between $min and $max."
    }

    override fun stringLengthRangeFailure(range: IntRange): String {
        return "The length of the value must be between ${range.first} and ${range.last}."
    }

    override val invalidDateFormatFailure: String = "Invalid date format (the only accepted are day/month/year)."
    override val invalidTimeFormatFailure: String = "Invalid time format (the only accepted are hours:minutes)."
    override val unknownError: String = "Unknown error."

    override val noItemsMessage: String = "No items."
    override val confirmButton: String = "Confirm"
    override val cancelButton: String = "Cancel"
}
