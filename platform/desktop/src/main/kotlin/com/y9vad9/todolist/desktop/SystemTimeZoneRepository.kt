package com.y9vad9.todolist.desktop

import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import java.time.ZoneId

class SystemTimeZoneRepository(
    pollingIntervalMillis: Long = 5_000L,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : TimeZoneRepository {
    private val _timeZone = MutableStateFlow(currentSystemTimeZone())
    override val timeZone: StateFlow<TimeZone> get() = _timeZone

    private val pollingJob = coroutineScope.launch {
        var lastZoneId = ZoneId.systemDefault()
        while (isActive) {
            delay(pollingIntervalMillis)
            val newZoneId = ZoneId.systemDefault()
            if (newZoneId != lastZoneId) {
                lastZoneId = newZoneId
                _timeZone.value = TimeZone.of(newZoneId.id) // Convert ZoneId to kotlinx.datetime.TimeZone
            }
        }
    }

    private fun currentSystemTimeZone(): TimeZone {
        return TimeZone.of(ZoneId.systemDefault().id) // Convert ZoneId to kotlinx.datetime.TimeZone
    }
}