package com.calendarflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single calendar event stored in the local Room database.
 *
 * @param id        Auto-generated primary key.
 * @param title     Short title shown on the calendar day.
 * @param note      Optional longer description.
 * @param dateEpoch The date of the event as a Unix epoch day
 *                  (LocalDate.toEpochDay()) — easy to query by date.
 */
@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val note: String = "",
    val dateEpoch: Long   // stored as epoch day for simple range queries
)
