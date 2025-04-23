package com.y9vad9.todolist.ios

import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import platform.Foundation.NSTimeZone
import platform.Foundation.systemTimeZone

open class IosTimeZoneProvider : TimeZoneRepository {
    private val scope: CoroutineScope = MainScope()
    private val _timeZone = MutableStateFlow(currentSystemTimeZone())
    override val timeZone: StateFlow<TimeZone> = _timeZone

    // Hold an Objective-C compatible observer as a delegate
    private val observer = TimeZoneChangeObserver { updatedZone ->
        _timeZone.value = updatedZone
    }

    init {
        observer.startObserving()
    }

    public fun dispose() {
        observer.stopObserving()
    }

    private fun currentSystemTimeZone(): TimeZone {
        return NSTimeZone.systemTimeZone.toKotlinTimeZone()
    }
}
