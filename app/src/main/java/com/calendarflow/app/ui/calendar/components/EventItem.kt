package com.calendarflow.app.ui.calendar.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.calendarflow.app.data.local.entity.EventEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A polished card that displays a single [EventEntity].
 *
 * Features:
 *  - Title (bold), description (muted, max 2 lines), "created at" timestamp
 *  - Delete icon that opens a confirmation [AlertDialog] before removing
 *  - Slide-in entrance animation when first composed
 *  - Adapts colours for light and dark mode via MaterialTheme
 *
 * @param event    The event to display.
 * @param onDelete Called with the event after the user confirms deletion.
 * @param modifier Optional modifier for the outer card.
 */
@Composable
fun EventItem(
    event: EventEntity,
    onDelete: (EventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Controls whether the confirmation dialog is visible
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ── Entrance animation ────────────────────────────────────────────────────
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 5.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                // ── Left accent icon ──────────────────────────────────────────
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // ── Text content ──────────────────────────────────────────────
                Column(modifier = Modifier.weight(1f)) {

                    // Title
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Description (optional)
                    if (event.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Created-at timestamp
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = event.createdAt.toFormattedTime(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ── Delete button ─────────────────────────────────────────────
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete event",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // ── Confirmation dialog ───────────────────────────────────────────────────
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            eventTitle = event.title,
            onConfirm = {
                showDeleteDialog = false
                onDelete(event)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

// ── Confirmation dialog ───────────────────────────────────────────────────────

/**
 * A simple Material 3 [AlertDialog] that asks the user to confirm deletion.
 *
 * Keeping this as a separate composable makes it easy to reuse elsewhere
 * and keeps [EventItem] focused on layout.
 */
@Composable
private fun DeleteConfirmationDialog(
    eventTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Delete event?",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = "\"$eventTitle\" will be permanently removed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/**
 * Formats a Unix-millis timestamp into a human-readable "h:mm a · MMM d" string.
 * Example: "9:41 AM · Jan 5"
 */
private fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("h:mm a · MMM d", Locale.getDefault())
    return sdf.format(Date(this))
}
