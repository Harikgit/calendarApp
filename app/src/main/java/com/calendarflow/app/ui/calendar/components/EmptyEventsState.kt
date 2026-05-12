package com.calendarflow.app.ui.calendar.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Shown below the calendar when a date is selected but has no events.
 *
 * Uses a gentle breathing animation on the icon to feel alive without
 * being distracting.
 *
 * @param message  Primary message line (e.g. "No events on this day").
 * @param hint     Secondary hint line (e.g. "Tap + to add one").
 */
@Composable
fun EmptyEventsState(
    message: String = "No events on this day",
    hint: String = "Tap + to add your first event",
    modifier: Modifier = Modifier
) {
    // Gentle breathing animation on the icon alpha
    val infiniteTransition = rememberInfiniteTransition(label = "emptyPulse")
    val iconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue  = 0.75f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1_800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.EventNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(64.dp)
                .alpha(iconAlpha)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text      = message,
            style     = MaterialTheme.typography.titleSmall,
            color     = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text      = hint,
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Shown when no date has been selected yet (initial state).
 */
@Composable
fun NoDateSelectedState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.EventNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text      = "Select a day to view events",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
