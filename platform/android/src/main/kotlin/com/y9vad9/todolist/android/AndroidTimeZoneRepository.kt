package com.y9vad9.todolist.android

import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.toKotlinTimeZone

class AndroidTimeZoneRepository(
    private val context: Context
) : TimeZoneRepository {

    private val _timeZone = MutableStateFlow(
        java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()
    )

    override val timeZone: StateFlow<TimeZone> get() = _timeZone

    private val timeZoneChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIMEZONE_CHANGED) {
                _timeZone.value = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()
            }
        }
    }

    init {
        context.registerReceiver(
            timeZoneChangedReceiver,
            IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
        )
    }

    fun clear() {
        context.unregisterReceiver(timeZoneChangedReceiver)
    }
}
