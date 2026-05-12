package com.calendarflow.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calendarflow.app.data.local.entity.EventEntity
import com.calendarflow.app.data.repository.EventRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for the Calendar screen.
 *
 * Drives [CalendarUiState] via [StateFlow] so the UI is always in sync with
 * the database without any manual refresh calls.
 *
 * Now backed by [EventRepository] (the "events" table / [EventEntity]).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val repository: EventRepository
) : ViewModel() {

    // ── Private mutable state ─────────────────────────────────────────────────

    /** Currently selected date. Defaults to today. */
    private val _selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now())

    // ── Derived flows ─────────────────────────────────────────────────────────

    /**
     * Switches to a new DB query every time the selected date changes.
     * [flatMapLatest] cancels the previous query automatically — no leaks.
     */
    private val eventsForSelectedDay = _selectedDate.flatMapLatest { date ->
        if (date != null) repository.getEventsForDay(date.toEpochDay())
        else flowOf(emptyList())
    }

    /** All days that have at least one event — drives dot indicators. */
    private val daysWithEvents = repository.getAllEventDays()

    // ── Public UI state ───────────────────────────────────────────────────────

    /**
     * Single source of truth for the Calendar screen.
     *
     * [combine] merges three flows into one [CalendarUiState]. Every time any
     * source flow emits, a fresh state object is produced and the UI recomposes.
     *
     * [stateIn] makes this a hot StateFlow that stays alive for 5 seconds after
     * the last subscriber (e.g. during a screen rotation) to avoid restarting
     * the DB queries unnecessarily.
     */
    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedDate,
        eventsForSelectedDay,
        daysWithEvents
    ) { selectedDate, events, eventDays ->
        CalendarUiState(
            selectedDate = selectedDate,
            today = LocalDate.now(),
            eventsForSelected = events,
            daysWithEvents = eventDays.toSet()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarUiState()
    )

    // ── User actions ──────────────────────────────────────────────────────────

    /** Called when the user taps a day cell on the calendar. */
    fun onDaySelected(date: LocalDate) {
        _selectedDate.value = date
    }

    /** Delete an event. Runs in a background coroutine. */
    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    class Factory(private val repository: EventRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
