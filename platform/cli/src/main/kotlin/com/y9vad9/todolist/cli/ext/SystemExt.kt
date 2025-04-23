@file:Suppress("UNUSED", "UnusedReceiverParameter")

package com.y9vad9.todolist.cli.ext

import java.lang.System.getenv
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

// we use System just to restrict namespace
val System.currentLocale: Locale get() =
    Locale.forLanguageTag(getenv("user.language"))
val System.currentTimeZone: ZonedDateTime get() = ZonedDateTime.now(ZoneId.systemDefault())