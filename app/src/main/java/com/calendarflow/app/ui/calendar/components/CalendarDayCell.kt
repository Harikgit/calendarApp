package com.calendarflow.app.ui.calendar.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate

/**
 * A single day cell in the calendar grid.
 *
 * Visual states (in priority order):
 *  1. Selected  → filled primary circle, white text, animates smoothly
 *  2. Today     → primary-coloured border ring, primary text (when not selected)
 *  3. In-month  → normal onSurface text
 *  4. Out-month → heavily muted text, not tappable
 *  5. Has event → small dot below the number (white on selected, primary otherwise)
 *
 * Animations:
 *  - Circle background colour animates with a spring when selection changes.
 *  - Text colour animates in sync.
 */
@Composable
fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvent: Boolean,
    onClick: (LocalDate) -> Unit
) {
    val isInMonth = day.position == DayPosition.MonthDate
    val primary   = MaterialTheme.colorScheme.primary

    // ── Animated colours ──────────────────────────────────────────────────────
    val targetBg = when {
        isSelected -> primary
        else       -> Color.Transparent
    }
    val animatedBg by animateColorAsState(
        targetValue = targetBg,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "dayBackground"
    )

    val targetTextColor = when {
        isSelected -> Color.White
        isToday    -> primary
        isInMonth  -> MaterialTheme.colorScheme.onSurface
        else       -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
    }
    val animatedText by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "dayText"
    )

    // ── Border: ring for today (only when not selected) ───────────────────────
    val borderModifier = if (isToday && !isSelected) {
        Modifier.border(width = 1.5.dp, color = primary, shape = CircleShape)
    } else {
        Modifier
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(CircleShape)
            .background(animatedBg)
            .then(borderModifier)
            .clickable(enabled = isInMonth) { onClick(day.date) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text       = day.date.dayOfMonth.toString(),
            color      = animatedText,
            fontSize   = 13.sp,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign  = TextAlign.Center
        )

        // Event dot — only for in-month days
        if (hasEvent && isInMonth) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else primary)
            )
        }
    }
}
