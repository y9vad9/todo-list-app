package com.y9vad9.todolist.domain.usecase

import com.y9vad9.todolist.domain.ext.localDateTimeFlow
import com.y9vad9.todolist.domain.ext.nextDay
import com.y9vad9.todolist.domain.ext.toKotlinInstantRange
import com.y9vad9.todolist.domain.repository.TaskRepository
import com.y9vad9.todolist.domain.repository.TimeZoneRepository
import com.y9vad9.todolist.domain.type.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.datetime.*
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

/**
 * This is an aggregate use case for getting important tasks. It will provide tasks that are due today,
 * tasks that are due tomorrow, tasks that are due this week and tasks that are due in the next week. It automatically
 * updates when the time zone or the current time changes (to the next day, to the next week, etc.).
 *
 * @param timeZoneRepository [TimeZoneRepository] for getting current time zone.
 * @param tasksRepository [TaskRepository] for getting tasks.
 * @param clock [Clock] for getting current time.
 *
 * @see Result
 * @see Task
 * @see TimeZoneRepository
 * @see TaskRepository
 */
class GetImportantTasksUseCase(
    private val timeZoneRepository: TimeZoneRepository,
    private val tasksRepository: TaskRepository,
    private val clock: Clock,
) {
    sealed interface Result {
        data class Error(val error: Throwable) : Result
        data class Success(
            val dueTasks: List<Task>,
            val tasksToday: List<Task>,
            val tasksNextDay: List<Task>,
            val tasksThisWeek: List<Task>,
            val tasksNextWeek: List<Task>,
        ) : Result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun execute(): Flow<Result> {
        val forceUpdates: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)
        forceUpdates.tryEmit(Unit)

        return clock.localDateTimeFlow(timeZoneRepository.timeZone)
            .combine(forceUpdates) { dateTime, _ ->
                dateTime
            }.flatMapLatest { currentLocalDateTime ->
                channelFlow<Result> {
                    while (isActive) {
                        val now = clock.now()
                        val currentDate = currentLocalDateTime.date
                        val timeZone = timeZoneRepository.timeZone.value

                        val todayRange = now.toLocalDateTime(timeZone)..currentDate.atTime(23, 59, 59)
                        val nextDayDate = currentDate.nextDay
                        val nextDayRange = nextDayDate.atTime(0, 0)..nextDayDate.atTime(23, 59, 59)

                        val dayAfterNext = nextDayDate.nextDay
                        val currentWeekDay = dayAfterNext.dayOfWeek.ordinal
                        val daysUntilSunday = DayOfWeek.SUNDAY.ordinal - currentWeekDay
                        val thisWeekRange = if (daysUntilSunday >= 0) {
                            val endOfWeekDate = dayAfterNext.plus(DatePeriod(days = daysUntilSunday))
                            dayAfterNext.atTime(0, 0)..endOfWeekDate.atTime(23, 59, 59)
                        } else null

                        val daysUntilNextMonday =
                            (DayOfWeek.MONDAY.ordinal - dayAfterNext.dayOfWeek.ordinal + 7) % 7
                        val nextMonday = dayAfterNext.plus(DatePeriod(days = daysUntilNextMonday))
                        val nextSunday = nextMonday.plus(DatePeriod(days = 6))
                        val nextWeekRange = nextMonday.atTime(0, 0)..nextSunday.atTime(23, 59, 59)

                        val dueTasksFlow = tasksRepository.getDueTasks(now)
                        val todayTasksFlow = tasksRepository.getTasksWithDueBetween(todayRange.toKotlinInstantRange(timeZone))
                        val nextDayTasksFlow = tasksRepository.getTasksWithDueBetween(nextDayRange.toKotlinInstantRange(timeZone))
                        val thisWeekTasksFlow = thisWeekRange?.let {
                            tasksRepository.getTasksWithDueBetween(it.toKotlinInstantRange(timeZone))
                        } ?: flow { emit(emptyList()) }
                        val nextWeekTasksFlow = tasksRepository.getTasksWithDueBetween(nextWeekRange.toKotlinInstantRange(timeZone))

                        combine(
                            dueTasksFlow,
                            todayTasksFlow,
                            nextDayTasksFlow,
                            thisWeekTasksFlow,
                            nextWeekTasksFlow
                        ) { dueTasks, todayTasks, nextDayTasks, thisWeekTasks, nextWeekTasks ->
                            Result.Success(
                                dueTasks = dueTasks,
                                tasksToday = todayTasks,
                                tasksNextDay = nextDayTasks,
                                tasksThisWeek = thisWeekTasks,
                                tasksNextWeek = nextWeekTasks
                            )
                        }
                            .collectLatest { result ->
                                send(result)

                                val allTasks = listOf(result.dueTasks, result.tasksToday, result.tasksNextDay, result.tasksThisWeek, result.tasksNextWeek)
                                val nextTriggerTime = allTasks
                                    .flatten()
                                    .minOfOrNull { it.due } ?: now.plus(1.days)

                                val delayMillis = (nextTriggerTime - now).inWholeMilliseconds.coerceAtLeast(1_000)

                                delay(delayMillis + 100)
                                forceUpdates.emit(Unit)
                            }
                    }
                }
            }
            .catch { emit(Result.Error(it)) }
    }

}