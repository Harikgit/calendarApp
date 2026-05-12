package com.calendarflow.app.ui.calendar

import com.calendarflow.app.data.local.entity.EventEntity
import java.time.LocalDate

/**
 * Immutable snapshot of everything the Calendar screen needs to render.
 *
 * The UI is always a pure function of this object — no hidden state anywhere.
 *
 * @param selectedDate       Day the user has tapped. Null = nothing selected yet.
 * @param today              Today's date — used to highlight the current day cell.
 * @param eventsForSelected  All [EventEntity] rows for [selectedDate], ordered by
 *                           creation time (ascending). Empty list when no events exist.
 * @param daysWithEvents     Set of epoch days that have ≥1 event. Used to draw
 *                           dot indicators on calendar day cells.
 * @param isLoading          True while an async operation (e.g. delete) is running.
 */
data class CalendarUiState(
    val selectedDate: LocalDate? = null,
    val today: LocalDate = LocalDate.now(),
    val eventsForSelected: List<EventEntity> = emptyList(),
    val daysWithEvents: Set<Long> = emptySet(),
    val isLoading: Boolean = false
) {
    /** Convenience — how many events exist for the selected day. */
    val eventCount: Int get() = eventsForSelected.size

    /** True when a date is selected and it has at least one event. */
    val hasEvents: Boolean get() = selectedDate != null && eventsForSelected.isNotEmpty()
}
