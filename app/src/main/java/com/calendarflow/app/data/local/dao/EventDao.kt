package com.calendarflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.calendarflow.app.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [EventEntity].
 *
 * Rules of thumb used here:
 *  - Functions that READ data return [Flow] so the UI reacts automatically
 *    whenever the database changes — no manual refresh needed.
 *  - Functions that WRITE data are `suspend` so they must be called from a
 *    coroutine (e.g. inside viewModelScope.launch { }).
 */
@Dao
interface EventDao {

    // ── Write operations ──────────────────────────────────────────────────────

    /**
     * Insert a new event.
     * Returns the new row's auto-generated [id].
     * [OnConflictStrategy.ABORT] means an exception is thrown if you try to
     * insert a duplicate primary key — safer than silently replacing data.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(event: EventEntity): Long

    /**
     * Update an existing event (matched by [EventEntity.id]).
     * Use this when the user edits a previously saved event.
     */
    @Update
    suspend fun update(event: EventEntity)

    /**
     * Delete a specific event.
     * Room matches the row by the entity's primary key.
     */
    @Delete
    suspend fun delete(event: EventEntity)

    /**
     * Delete all events on a given date.
     * Handy when the user clears an entire day.
     */
    @Query("DELETE FROM events WHERE selectedDate = :epochDay")
    suspend fun deleteAllForDay(epochDay: Long)

    // ── Read operations ───────────────────────────────────────────────────────

    /**
     * Observe all events for a specific date, ordered by creation time.
     * Emits a new list every time any event on that day is added/updated/deleted.
     */
    @Query("SELECT * FROM events WHERE selectedDate = :epochDay ORDER BY createdAt ASC")
    fun getEventsForDay(epochDay: Long): Flow<List<EventEntity>>

    /**
     * Observe all distinct epoch days that have at least one event.
     * The calendar screen uses this to draw dot indicators on days.
     */
    @Query("SELECT DISTINCT selectedDate FROM events")
    fun getAllEventDays(): Flow<List<Long>>

    /**
     * Observe every event in the database, newest first.
     * Useful for a global "all events" list screen.
     */
    @Query("SELECT * FROM events ORDER BY selectedDate ASC, createdAt ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    /**
     * One-shot fetch of a single event by id.
     * `suspend` (not Flow) because we only need the value once, e.g. to
     * pre-fill an edit form.
     */
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): EventEntity?
}
