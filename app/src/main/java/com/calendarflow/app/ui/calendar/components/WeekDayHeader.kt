package com.calendarflow.app.ui.calendar.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

/**
 * A row of abbreviated weekday labels (Mon, Tue, … Sun) shown above the day grid.
 *
 * [daysOfWeek] from the Kizitonwose library returns the days in the correct
 * order based on the device locale (e.g. Sunday-first in the US).
 *
 * @param firstDayOfWeek The first day of the week to use (defaults to Monday).
 */
@Composable
fun WeekDayHeader(
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    modifier: Modifier = Modifier
) {
    // daysOfWeek() returns [Mon, Tue, Wed, Thu, Fri, Sat, Sun] when starting on Monday
    val days = daysOfWeek(firstDayOfWeek = firstDayOfWeek)

    Row(modifier = modifier.fillMaxWidth()) {
        for (day in days) {
            Text(
                modifier = Modifier.weight(1f),
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
