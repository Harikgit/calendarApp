package com.calendarflow.app

import android.app.Application
import com.calendarflow.app.data.local.database.CalendarDatabase
import com.calendarflow.app.data.repository.CalendarRepository
import com.calendarflow.app.data.repository.EventRepository

/**
 * Custom [Application] class — created once for the lifetime of the process.
 *
 * This is the right place to initialise app-wide singletons like the database
 * and repositories. Using `by lazy` means they are only created the first time
 * they are accessed, not at app startup.
 *
 * Registered in AndroidManifest.xml via android:name=".CalendarFlowApplication".
 */
class CalendarFlowApplication : Application() {

    /** The single Room database instance. */
    val database: CalendarDatabase by lazy {
        CalendarDatabase.getInstance(this)
    }

    /**
     * Legacy repository backed by the original "calendar_events" table.
     * Kept so the existing CalendarScreen continues to work unchanged.
     */
    val repository: CalendarRepository by lazy {
        CalendarRepository(database.calendarEventDao())
    }

    /**
     * New repository backed by the "events" table (EventEntity).
     * Used by AddEventScreen and the updated CalendarViewModel.
     */
    val eventRepository: EventRepository by lazy {
        EventRepository(database.eventDao())
    }
}
