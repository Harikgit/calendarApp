package com.calendarflow.app.data.repository

import com.calendarflow.app.data.local.dao.CalendarEventDao
import com.calendarflow.app.data.local.entity.CalendarEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository — the single source of truth for calendar data.
 *
 * The repository sits between the ViewModel and the data sources (Room, network, etc.).
 * ViewModels never talk to the DAO directly; they always go through the repository.
 * This makes it easy to swap data sources later without touching the UI layer.
 */
class CalendarRepository(private val dao: CalendarEventDao) {

    /** Stream of events for a specific day. Emits whenever the DB changes. */
    fun getEventsForDay(epochDay: Long): Flow<List<CalendarEvent>> =
        dao.getEventsForDay(epochDay)

    /** Stream of all days that have at least one event (for dot indicators). */
    fun getAllEventDays(): Flow<List<Long>> =
        dao.getAllEventDays()

    /** Stream of every event ordered by date. */
    fun getAllEvents(): Flow<List<CalendarEvent>> =
        dao.getAllEvents()

    /** Add a new event. Runs on a coroutine (suspend). */
    suspend fun addEvent(event: CalendarEvent): Long =
        dao.insert(event)

    /** Update an existing event. */
    suspend fun updateEvent(event: CalendarEvent) =
        dao.update(event)

    /** Remove an event. */
    suspend fun deleteEvent(event: CalendarEvent) =
        dao.delete(event)
}
