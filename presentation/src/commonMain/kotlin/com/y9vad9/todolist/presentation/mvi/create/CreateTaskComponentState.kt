package com.y9vad9.todolist.presentation.mvi.create

import com.y9vad9.todolist.domain.type.value.TaskDescription
import com.y9vad9.todolist.domain.type.value.TaskName
import com.y9vad9.todolist.presentation.validation.Input
import com.y9vad9.todolist.presentation.validation.input
import com.y9vad9.todolist.presentation.validation.isValid
import com.y9vad9.todolist.presentation.validation.mappers.LocalDateFromStringFactory
import com.y9vad9.todolist.presentation.validation.mappers.LocalTimeFromStringFactory
import pro.respawn.flowmvi.api.MVIState

data class CreateTaskComponentState(
    val name: Input<String> = input(""),
    val description: Input<String> = input(""),
    val dueDate: Input<String> = input(""),
    val dueTime: Input<String> = input("00:00"),
    val isLoading: Boolean = false,
) : MVIState {
    fun validated(): CreateTaskComponentState {
        return copy(
            name = name.validated(TaskName.factory),
            description = description.validated(TaskDescription.factory),
            dueDate = dueDate.validated(LocalDateFromStringFactory),
            dueTime = dueTime.validated(LocalTimeFromStringFactory),
        )
    }

    fun isValid(): Boolean {
        return name.isValid() && description.isValid() && dueDate.isValid() && dueTime.isValid()
    }
}