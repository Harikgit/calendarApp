package com.calendarflow.app.data.repository

import com.calendarflow.app.data.local.dao.EventDao
import com.calendarflow.app.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for [EventEntity] data.
 *
 * This is the single source of truth for the new "events" table.
 * ViewModels always talk to the repository — never directly to the DAO.
 * This separation makes it easy to add caching, network sync, or other
 * data sources later without touching the UI layer.
 *
 * @param dao The Room DAO injected from [CalendarDatabase].
 */
class EventRepository(private val dao: EventDao) {

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Observe all events for a given date (as epoch day).
     * Returns a [Flow] — the UI will automatically update whenever
     * events are added, changed, or removed for that day.
     */
    fun getEventsForDay(epochDay: Long): Flow<List<EventEntity>> =
        dao.getEventsForDay(epochDay)

    /**
     * Observe all distinct days that have at least one event.
     * Used by the calendar to show dot indicators.
     */
    fun getAllEventDays(): Flow<List<Long>> =
        dao.getAllEventDays()

    /**
     * Observe every event in the database.
     * Useful for a future "all events" list screen.
     */
    fun getAllEvents(): Flow<List<EventEntity>> =
        dao.getAllEvents()

    /**
     * One-shot fetch of a single event by its id.
     * Returns null if no event with that id exists.
     * Use this to pre-fill an edit form.
     */
    suspend fun getEventById(id: Long): EventEntity? =
        dao.getEventById(id)

    // ── Write ─────────────────────────────────────────────────────────────────

    /**
     * Insert a new event and return its auto-generated id.
     * The [createdAt] timestamp is set automatically inside [EventEntity].
     */
    suspend fun insertEvent(event: EventEntity): Long =
        dao.insert(event)

    /**
     * Update an existing event (matched by [EventEntity.id]).
     */
    suspend fun updateEvent(event: EventEntity) =
        dao.update(event)

    /**
     * Delete a specific event.
     */
    suspend fun deleteEvent(event: EventEntity) =
        dao.delete(event)

    /**
     * Delete every event on a given date.
     */
    suspend fun deleteAllForDay(epochDay: Long) =
        dao.deleteAllForDay(epochDay)
}
