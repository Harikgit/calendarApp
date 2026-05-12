package com.calendarflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity — maps directly to the "events" table in the database.
 *
 * Field naming follows the spec:
 *  - [id]           Auto-generated primary key.
 *  - [title]        Short event title (required, validated before insert).
 *  - [description]  Optional longer note about the event.
 *  - [selectedDate] The date this event belongs to, stored as a Unix epoch day
 *                   (LocalDate.toEpochDay()). Epoch days are plain Long values,
 *                   so Room can store them without a custom TypeConverter.
 *  - [createdAt]    Timestamp (System.currentTimeMillis()) recorded when the
 *                   event is first created. Useful for sorting and auditing.
 */
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val description: String = "",

    /** Epoch day — use LocalDate.toEpochDay() / LocalDate.ofEpochDay() to convert. */
    val selectedDate: Long,

    /** Unix millis — System.currentTimeMillis() at insert time. */
    val createdAt: Long = System.currentTimeMillis()
)
