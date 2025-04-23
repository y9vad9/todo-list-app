package com.y9vad9.todolist.composeui.localization

import androidx.compose.runtime.compositionLocalOf

interface Strings {
    val appName: String get() = "TodoList"
    // -- Create Task Screen
    val createTaskTitle: String
    val taskNameTitle: String
    val taskDescriptionTitle: String
    val taskDateTitle: String
    val taskTimeOfTheDayTitle: String
    val createTaskButton: String

    // -- Region end --

    // -- Edit Task Screen --
    val editTaskTitle: String
    val deleteTaskButton: String
    val editTaskButton: String

    // -- Region end --

    // -- View Task Screen --
    val viewTaskTitle: String
    val viewTaskButton: String
    val editTaskButtonDescription: String

    val dueToTitle: String
    val createdAtTitle: String
    val statusTitle: String

    val startedAtTitle: String
    val finishedAtTitle: String

    val markAsInProgressButton: String
    val markAsCompletedButton: String

    val completedAtTitle: String

    // -- Region end --

    // -- Important Screen --
    val overdueTasksTitle: String
    val tasksDueTodayTitle: String
    val tasksUntilTomorrowTitle: String
    val tasksThisWeekTitle: String
    val tasksNextWeekTitle: String

    val importantTitle: String

    val noItemsInImportantYetMessage: String


    // -- Region end --

    // -- Settings Screen --
    val appLanguageTitle: String
    val appThemeTitle: String
    val currentLanguageTitle: String

    val lightThemeTitle: String
    val darkThemeTitle: String
    val systemThemeTitle: String

    val settingsTitle: String

    // -- Region end --

    val completedTitle: String
    val inProgressTitle: String
    val planedTitle: String

    fun overdueBy(value: String): String
    fun dueIn(value: String): String
    fun completedEarlyBy(value: String): String

    fun seconds(value: Int): String
    fun minutes(value: Int): String
    fun hours(value: Int): String
    fun days(value: Int): String
    fun weeks(value: Int): String
    fun months(value: Int): String
    fun years(value: Int): String

    fun internalErrorMessage(t: Throwable): String
    val failureMessage: String

    val dateCannotBeInPastMessage: String

    val goBackActionDescription: String

    val allTasksTitle: String
    val filterTitle: String

    // -- Validation --
    fun maxNumberValueFailure(max: Number): String
    fun minNumberValueFailure(min: Number): String
    fun numberRangeFailure(min: Number, max: Number): String
    fun stringLengthRangeFailure(range: IntRange): String
    val invalidDateFormatFailure: String
    val invalidTimeFormatFailure: String
    val unknownError: String

    val noItemsMessage: String

    val confirmButton: String
    val cancelButton: String
}

val LocalStrings = compositionLocalOf<Strings> { error("Strings is not provided.") }