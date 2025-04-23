package com.y9vad9.todolist.ios

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.ObjCProtocol
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import platform.Foundation.NSNotification
import platform.darwin.NSObject
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSSystemTimeZoneDidChangeNotification
import platform.Foundation.NSTimeZone
import platform.Foundation.systemTimeZone

@OptIn(ExperimentalForeignApi::class)
class TimeZoneChangeObserver(
    private val onChange: (TimeZone) -> Unit
) : NSObject() {
    
    fun startObserving() {
        NSNotificationCenter.defaultCenter.addObserver(
            observer = this,
            selector = NSSelectorFromString("onSystemTimeZoneDidChange:"),
            name = NSSystemTimeZoneDidChangeNotification,
            `object` = null
        )
    }

    fun stopObserving() {
        NSNotificationCenter.defaultCenter.removeObserver(this)
    }

    @ObjCAction
    @Suppress("UNUSED_PARAMETER")
    fun onSystemTimeZoneDidChange(notification: NSNotification) {
        onChange(NSTimeZone.systemTimeZone.toKotlinTimeZone())
    }

    override fun finalize() {
        stopObserving()
    }
}
