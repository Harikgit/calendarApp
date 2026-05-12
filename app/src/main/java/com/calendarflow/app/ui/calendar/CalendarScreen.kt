package com.calendarflow.app.ui.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calendarflow.app.ui.calendar.components.CalendarDayCell
import com.calendarflow.app.ui.calendar.components.CalendarHeader
import com.calendarflow.app.ui.calendar.components.EmptyEventsState
import com.calendarflow.app.ui.calendar.components.EventItem
import com.calendarflow.app.ui.calendar.components.NoDateSelectedState
import com.calendarflow.app.ui.calendar.components.WeekDayHeader
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Main Calendar screen — the home screen of CalendarFlow.
 *
 * Layout (top → bottom):
 *  ┌──────────────────────────────────────┐
 *  │  TopAppBar  "CalendarFlow"           │
 *  ├──────────────────────────────────────┤
 *  │  CalendarCard                        │
 *  │    ← MMMM yyyy →   (animated)       │
 *  │    Mon Tue Wed Thu Fri Sat Sun       │
 *  │    [ calendar grid ]                 │
 *  ├──────────────────────────────────────┤
 *  │  Section header  "EEEE, d MMMM"     │
 *  │    (animated slide when date changes)│
 *  ├──────────────────────────────────────┤
 *  │  LazyColumn of EventItem cards       │
 *  │    — or EmptyEventsState             │
 *  │    — or NoDateSelectedState          │
 *  └──────────────────────────────────────┘
 *                                  [FAB +]
 *
 * @param viewModel   Drives the UI state via StateFlow.
 * @param onAddEvent  Called with the selected epoch day → navigates to AddEventScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onAddEvent: (epochDay: Long) -> Unit
) {
    // ── State ─────────────────────────────────────────────────────────────────
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── Calendar library setup ────────────────────────────────────────────────
    val currentMonth   = remember { YearMonth.now() }
    val startMonth     = remember { currentMonth.minusMonths(100) }
    val endMonth       = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { DayOfWeek.MONDAY }

    val calendarState = rememberCalendarState(
        startMonth       = startMonth,
        endMonth         = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek   = firstDayOfWeek
    )
    val scope = rememberCoroutineScope()

    // ── Scaffold ──────────────────────────────────────────────────────────────
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CalendarFlow",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            // FAB appears only when a date is selected, with a fade animation
            AnimatedVisibility(
                visible = uiState.selectedDate != null,
                enter   = fadeIn() + slideInVertically { it },
                exit    = fadeOut() + slideOutVertically { it }
            ) {
                FloatingActionButton(
                    onClick = { uiState.selectedDate?.let { onAddEvent(it.toEpochDay()) } },
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add event",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Calendar card ─────────────────────────────────────────────────
            item(key = "calendar_card") {
                CalendarCard(
                    calendarState = calendarState,
                    uiState       = uiState,
                    firstDayOfWeek = firstDayOfWeek,
                    onPrevMonth = {
                        scope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth.minusMonths(1)
                            )
                        }
                    },
                    onNextMonth = {
                        scope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth.plusMonths(1)
                            )
                        }
                    },
                    onDayClick = viewModel::onDaySelected
                )
            }

            // ── Section header ────────────────────────────────────────────────
            item(key = "section_header") {
                Spacer(modifier = Modifier.height(16.dp))
                EventsSectionHeader(selectedDate = uiState.selectedDate)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Events / empty states ─────────────────────────────────────────
            when {
                // No date selected yet
                uiState.selectedDate == null -> {
                    item(key = "no_date") {
                        NoDateSelectedState()
                    }
                }

                // Date selected but no events
                uiState.eventsForSelected.isEmpty() -> {
                    item(key = "empty") {
                        EmptyEventsState()
                    }
                }

                // Events exist — render each card
                else -> {
                    items(
                        items = uiState.eventsForSelected,
                        key   = { it.id }   // stable key → no unnecessary recompositions
                    ) { event ->
                        EventItem(
                            event    = event,
                            onDelete = viewModel::deleteEvent
                        )
                    }
                }
            }

            // Bottom padding so the FAB never covers the last card
            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

/**
 * The calendar wrapped in a surface card with rounded corners and a subtle
 * shadow, visually separating it from the events list below.
 */
@Composable
private fun CalendarCard(
    calendarState: CalendarState,
    uiState: CalendarUiState,
    firstDayOfWeek: DayOfWeek,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {

            // Month navigation header
            CalendarHeader(
                currentMonth = calendarState.firstVisibleMonth.yearMonth,
                onPrevMonth  = onPrevMonth,
                onNextMonth  = onNextMonth,
                modifier     = Modifier.padding(horizontal = 8.dp)
            )

            // Weekday labels
            WeekDayHeader(
                firstDayOfWeek = firstDayOfWeek,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                color    = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Calendar grid
            HorizontalCalendar(
                state    = calendarState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                dayContent = { day: CalendarDay ->
                    CalendarDayCell(
                        day        = day,
                        isSelected = day.date == uiState.selectedDate,
                        isToday    = day.date == uiState.today,
                        hasEvent   = uiState.daysWithEvents.contains(day.date.toEpochDay()),
                        onClick    = onDayClick
                    )
                }
            )
        }
    }
}

/**
 * Section header that shows the selected date in a prominent style.
 * Animates with a vertical slide when the date changes.
 */
@Composable
private fun EventsSectionHeader(selectedDate: LocalDate?) {
    val dayFormatter  = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coloured accent bar
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 22.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        AnimatedContent(
            targetState = selectedDate,
            transitionSpec = {
                slideInVertically { -it } + fadeIn() togetherWith
                        slideOutVertically { it } + fadeOut()
            },
            label = "sectionHeader"
        ) { date ->
            if (date != null) {
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text  = date.format(dayFormatter),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text  = date.format(dateFormatter),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text  = "Events",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}
