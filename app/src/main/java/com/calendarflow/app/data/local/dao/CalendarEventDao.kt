package com.calendarflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.calendarflow.app.data.local.entity.CalendarEvent
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [CalendarEvent].
 *
 * All functions that return [Flow] are automatically observed — Room will
 * push a new emission whenever the underlying table changes.
 */
@Dao
interface CalendarEventDao {

    /** Insert a new event. If there is a conflict on the primary key, replace it. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CalendarEvent): Long

    /** Update an existing event. */
    @Update
    suspend fun update(event: CalendarEvent)

    /** Delete an event. */
    @Delete
    suspend fun delete(event: CalendarEvent)

    /** Observe all events for a specific date (by epoch day). */
    @Query("SELECT * FROM calendar_events WHERE dateEpoch = :epochDay ORDER BY id ASC")
    fun getEventsForDay(epochDay: Long): Flow<List<CalendarEvent>>

    /**
     * Observe all distinct epoch days that have at least one event.
     * Used to show dot indicators on calendar days.
     */
    @Query("SELECT DISTINCT dateEpoch FROM calendar_events")
    fun getAllEventDays(): Flow<List<Long>>

    /** Observe every event, ordered by date. */
    @Query("SELECT * FROM calendar_events ORDER BY dateEpoch ASC")
    fun getAllEvents(): Flow<List<CalendarEvent>>
}
