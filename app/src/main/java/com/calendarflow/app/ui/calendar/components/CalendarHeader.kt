package com.calendarflow.app.ui.calendar.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Month navigation header with an animated month/year label.
 *
 * The label slides left when going forward and right when going back,
 * giving a clear directional cue without being heavy.
 *
 * @param currentMonth  The [YearMonth] currently visible.
 * @param onPrevMonth   Called when the user taps ←.
 * @param onNextMonth   Called when the user taps →.
 */
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    // Track direction so the slide animation matches the navigation direction
    var goingForward by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ← Previous
        IconButton(
            onClick = {
                goingForward = false
                onPrevMonth()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Animated month label
        AnimatedContent(
            targetState = currentMonth,
            transitionSpec = {
                if (goingForward) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            },
            label = "monthLabel"
        ) { month ->
            Text(
                text = month.format(formatter),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // → Next
        IconButton(
            onClick = {
                goingForward = true
                onNextMonth()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
