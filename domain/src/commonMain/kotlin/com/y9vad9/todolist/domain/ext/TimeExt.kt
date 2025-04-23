package com.y9vad9.todolist.domain.ext

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal val LocalDate.nextDay: LocalDate get() =
    LocalDate(year, month, dayOfMonth).plus(DatePeriod(days = 1))

/**
 * Emits the current [LocalDateTime] in the given [TimeZone], updating at each local midnight
 * or whenever the [timeZoneFlow] emits a new [TimeZone].
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun Clock.localDateTimeFlow(timeZoneFlow: StateFlow<TimeZone>): Flow<LocalDateTime> =
    timeZoneFlow.flatMapLatest { tz ->
        flow {
            while (true) {
                val nowInstant = this@localDateTimeFlow.now()
                val nowLocal = nowInstant.toLocalDateTime(tz)
                emit(nowLocal)

                // calculate next local midnight
                val nextMidnightLocal = nowLocal.date.nextDay.atTime(0, 0)
                val nextMidnightInstant = nextMidnightLocal.toInstant(tz)
                val delayMillis = (nextMidnightInstant.minus(nowInstant)).inWholeMilliseconds

                delay(delayMillis)
            }
        }
    }

internal fun ClosedRange<LocalDateTime>.toKotlinInstantRange(timeZone: TimeZone): ClosedRange<Instant> {
    return start.toInstant(timeZone)..endInclusive.toInstant(timeZone)
}