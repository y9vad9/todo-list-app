package com.y9vad9.todolist.composeui.localization

object GermanStrings : Strings {
    override val appName = "TodoList"

    // -- Create Task Screen
    override val createTaskTitle = "Aufgabe erstellen"
    override val taskNameTitle = "Aufgabenname"
    override val taskDescriptionTitle = "Beschreibung"
    override val taskDateTitle = "Fälligkeitsdatum"
    override val taskTimeOfTheDayTitle: String = "Fällige Tageszeit"
    override val createTaskButton = "Erstellen"

    // -- Edit Task Screen --
    override val editTaskTitle = "Aufgabe bearbeiten"
    override val deleteTaskButton = "Löschen"
    override val editTaskButton = "Änderungen speichern"

    // -- View Task Screen --
    override val viewTaskTitle = "Aufgabendetails"
    override val viewTaskButton = "Anzeigen"
    override val editTaskButtonDescription = "Bearbeiten"
    override val dueToTitle = "Fällig am"
    override val createdAtTitle = "Erstellt am"
    override val statusTitle = "Status"
    override val startedAtTitle = "Begonnen am"
    override val finishedAtTitle = "Abgeschlossen am"

    override val markAsInProgressButton: String = "Als in Bearbeitung markieren"
    override val markAsCompletedButton: String = "Als abgeschlossen markieren"
    override val completedAtTitle: String = "Abgeschlossen am"


    // -- Important Screen --
    override val overdueTasksTitle = "Überfällige Aufgaben"
    override val tasksDueTodayTitle: String = "Heute fällig"
    override val tasksUntilTomorrowTitle = "Fällig bis morgen"
    override val tasksThisWeekTitle = "Fällig diese Woche"
    override val tasksNextWeekTitle = "Fällig nächste Woche"
    override val importantTitle = "Wichtig"

    override val noItemsInImportantYetMessage: String = "Es scheinen keine Elemente wichtig zu sein. Hier werden die anstehenden Aufgaben angezeigt."


    // -- Settings Screen --
    override val appLanguageTitle = "Sprache"
    override val appThemeTitle = "Design"
    override val currentLanguageTitle = "Deutsch"
    override val lightThemeTitle = "Hell"
    override val darkThemeTitle = "Dunkel"
    override val systemThemeTitle = "System"
    override val settingsTitle = "Einstellungen"

    // -- Task Categories --
    override val completedTitle = "Abgeschlossen"
    override val inProgressTitle = "In Bearbeitung"
    override val planedTitle = "Geplant"

    // -- Relative Time Helpers --
    override fun overdueBy(value: String) = "$value überfällig"
    override fun dueIn(value: String) = "Fällig in $value"
    override fun completedEarlyBy(value: String): String {
        return "Früher abgeschlossen um $value"
    }


    override fun seconds(value: Int) = if (value == 1) "1 Sekunde" else "$value Sekunden"
    override fun minutes(value: Int) = if (value == 1) "1 Minute" else "$value Minuten"
    override fun hours(value: Int) = if (value == 1) "1 Stunde" else "$value Stunden"
    override fun days(value: Int) = if (value == 1) "1 Tag" else "$value Tage"
    override fun weeks(value: Int) = if (value == 1) "1 Woche" else "$value Wochen"
    override fun months(value: Int) = if (value == 1) "1 Monat" else "$value Monate"
    override fun years(value: Int) = if (value == 1) "1 Jahr" else "$value Jahre"

    // -- Error & Feedback --
    override fun internalErrorMessage(t: Throwable) = "Ein interner Fehler ist aufgetreten: ${t.message ?: "unbekannter Fehler"}"
    override val failureMessage = "Vorgang fehlgeschlagen. Bitte versuche es erneut."

    // -- Validation & Actions --
    override val dateCannotBeInPastMessage = "Das Datum kann nicht in der Vergangenheit liegen."
    override val goBackActionDescription = "Zurück"

    // -- All Tasks Screen --
    override val allTasksTitle = "Alle Aufgaben"
    override val filterTitle: String = "Filter"

    // -- Validation --
    override fun maxNumberValueFailure(max: Number): String {
        return "Der Wert darf nicht größer als $max sein."
    }

    override fun minNumberValueFailure(min: Number): String {
        return "Der Wert darf nicht kleiner als $min sein."
    }

    override fun numberRangeFailure(min: Number, max: Number): String {
        return "Der Wert muss zwischen $min und $max liegen."
    }

    override fun stringLengthRangeFailure(range: IntRange): String {
        return "Die Länge des Wertes muss zwischen ${range.first} und ${range.last} liegen."
    }

    override val invalidDateFormatFailure: String = "Ungültiges Datumsformat (nur Tag/Monat/Jahr ist erlaubt)."
    override val invalidTimeFormatFailure: String = "Ungültiges Zeitformat (nur Stunden:Minuten ist erlaubt)."
    override val unknownError: String = "Unbekannter Fehler."

    override val noItemsMessage: String = "Keine Elemente."
    override val confirmButton: String = "Bestätigen"
    override val cancelButton: String = "Abbrechen"
}
