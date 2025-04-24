package com.y9vad9.todolist.cli.localization

import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.domain.type.value.TaskName

object GermanStrings : Strings {
    override val listCommand: Strings.ListCommand = object : Strings.ListCommand {
        override val shouldOnlyPrintDueTasksOptionDescription: String =
            "Gibt an, ob nur überfällige Aufgaben angezeigt werden sollen."

        override val categoryOptionDescription: String =
            "Liste von Aufgabentypen zum Anzeigen. Standardmäßig werden alle Aufgaben angezeigt."

        override val optionPageNumberDescription: String =
            "Gibt die Seitenzahl an."
        override val filterOptionDescription: String = "Filtern Sie Aufgaben nach Name/Beschreibung."

        override fun currentPageAndPagesLeft(currentPage: Int, amount: Int): String {
            return "Seite $currentPage von $amount insgesamt."
        }
    }

    override val editCommand: Strings.EditCommand = object : Strings.EditCommand {
        override val taskNameOptionDescription: String = "Neuer Aufgabenname (oder leer lassen, um ihn nicht zu ändern)"
        override val taskDescriptionOptionDescription: String = "Neue Aufgabenbeschreibung (oder leer lassen, um sie nicht zu ändern)"
        override val taskDueOptionDescription: String = "Neues Fälligkeitsdatum (oder leer lassen, um es nicht zu ändern)"
        override val promptTaskDescriptionMessage: String =
            "Gib eine Aufgabenbeschreibung ein (mehrzeilig; schreibe :q zum Beenden; leer lassen, um sie nicht zu ändern):"
    }

    override val taskIdOptionDescription: String =
        "Die ID der Aufgabe. Darf nicht negativ sein."

    override val taskNameOptionDescription: String =
        "Der Name der Aufgabe. Die Länge muss zwischen ${TaskName.LENGTH_RANGE} Zeichen liegen."

    override val taskDescriptionOptionDescription: String =
        "Die Beschreibung der Aufgabe. Unterstützt Markdown. Maximal 10.000 Zeichen."

    override val taskDueOptionDescription: String =
        "Gibt das Fälligkeitsdatum und die Uhrzeit der Aufgabe an."

    override val idTitle: String = "ID"
    override val nameTitle: String = "Name"
    override val dueOrOverdueTitle: String = "Fällig / Überfällig"
    override val categoryTitle: String = "Kategorie"
    override val createdAtTitle: String = "Erstellt am"

    override fun seconds(amount: Int): String {
        return "10 Sekunden"
    }

    override fun minutes(amount: Int): String =
        if (amount == 1) "$amount Minute" else "$amount Minuten"

    override fun hours(amount: Int): String =
        if (amount == 1) "$amount Stunde" else "$amount Stunden"

    override fun days(amount: Int): String =
        if (amount == 1) "$amount Tag" else "$amount Tage"

    override fun weeks(amount: Int): String =
        if (amount == 1) "$amount Woche" else "$amount Wochen"

    override fun months(amount: Int): String =
        if (amount == 1) "$amount Monat" else "$amount Monate"

    override fun years(amount: Int): String =
        if (amount == 1) "$amount Jahr" else "$amount Jahre"

    override val startedAtTitle: String = "Gestartet am"
    override val completedAtTitle: String = "Abgeschlossen am"
    override val timeSpent: String = "Benötigte Zeit"

    override fun tasksDue(amount: Int): String =
        "$amount fällige Aufgaben"

    override val planedTitle: String = "Geplant"
    override val inProgressTitle: String = "In Bearbeitung"
    override val completedTitle: String = "Abgeschlossen"

    override fun dueIn(formatted: String): String =
        "Noch $formatted"

    override fun dueFor(formatted: String): String =
        "Seit $formatted überfällig"

    override fun wasDueFor(formatted: String): String {
        return "War fällig für $formatted"
    }

    override fun wasCompletedEarlier(formatted: String): String {
        return "$formatted früher fertig"
    }

    override fun internalErrorMessage(t: Throwable): String =
        "Ein interner Fehler ist aufgetreten: ${t.message}"

    override val taskNotFoundMessage: String =
        "Aufgabe mit der angegebenen ID wurde nicht gefunden."

    override val idCannotBeNegativeMessage: String =
        "Die ID darf nicht negativ sein."

    override val taskNameLengthIsInvalid: String =
        "Aufgabename ist ungültig. Er muss zwischen ${TaskName.LENGTH_RANGE} Zeichen lang sein."

    override val taskDescriptionLengthIsInvalid: String =
        "Aufgabenbeschreibung ist ungültig. Sie muss zwischen ${TaskDescription.LENGTH_RANGE} Zeichen lang sein."

    override val taskDueFormatIsInvalid: String =
        "Ungültiges Fälligkeitsformat. Erlaubte Formate: yyyy-MM-dd HH:mm, yyyy-MM-dd, yyyy/MM/dd HH:mm, MMM d, yyyy, MMMM d, yyyy."

    override val taskDueCannotBeInPast: String =
        "Das Fälligkeitsdatum darf nicht in der Vergangenheit liegen."
    override val taskAlreadyStartedMessage: String = "Die Aufgabe wurde bereits gestartet."
    override val taskAlreadyCompletedMessage: String = "Die Aufgabe ist bereits erledigt."

    override fun taskCreatedMessage(id: TaskId): String =
        "Aufgabe wurde erfolgreich erstellt mit ID: $id."

    override val shouldBeStartedFirstMessage: String =
        "Die Aufgabe kann nicht abgeschlossen werden, bevor sie gestartet wurde."

    override val noTaskDescriptionProvidedMessage: String =
        "Keine Beschreibung für diese Aufgabe angegeben."

    override val promptTaskDescriptionMessage: String =
        "Bitte eine Aufgabenbeschreibung eingeben (optional):"
}
