package com.y9vad9.todolist.domain.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone

interface TimeZoneRepository {
    val timeZone: StateFlow<TimeZone>
}