package com.calendarflow.app.ui.addevent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Add Event screen.
 *
 * Layout:
 *  ┌──────────────────────────────────┐
 *  │  ← TopAppBar  "Add Event"        │
 *  ├──────────────────────────────────┤
 *  │  📅 Date chip (read-only)        │
 *  │                                  │
 *  │  Title field *                   │
 *  │    ↳ inline error when blank     │
 *  │                                  │
 *  │  Description field (optional)    │
 *  │                                  │
 *  │  [  ✓  Save Event  ]  button     │
 *  └──────────────────────────────────┘
 *
 * @param viewModel       Drives form state and save logic.
 * @param onNavigateBack  Called on ← tap or after a successful save.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: AddEventViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState          by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // React to save result — navigate back on success, show snackbar on error
    LaunchedEffect(uiState.saveResult) {
        when (val result = uiState.saveResult) {
            is SaveResult.Success -> {
                viewModel.onSaveResultConsumed()
                onNavigateBack()
            }
            is SaveResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.onSaveResultConsumed()
            }
            SaveResult.Idle -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Event",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = MaterialTheme.colorScheme.surface,
                    titleContentColor      = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Date chip ─────────────────────────────────────────────────────
            DateChip(date = viewModel.selectedDate)

            // ── Title field ───────────────────────────────────────────────────
            Column {
                OutlinedTextField(
                    value         = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label         = { Text("Title *") },
                    placeholder   = { Text("e.g. Team meeting") },
                    singleLine    = true,
                    isError       = uiState.titleError != null,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth()
                )
                // Inline error — slides in when validation fails
                AnimatedVisibility(
                    visible = uiState.titleError != null,
                    enter   = expandVertically() + fadeIn()
                ) {
                    Text(
                        text     = uiState.titleError ?: "",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            // ── Description field ─────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label         = { Text("Description") },
                placeholder   = { Text("Optional notes…") },
                minLines      = 3,
                maxLines      = 6,
                shape         = RoundedCornerShape(12.dp),
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Save button ───────────────────────────────────────────────────
            Button(
                onClick  = viewModel::saveEvent,
                enabled  = !uiState.isSaving,
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text  = "Save Event",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── Date chip ─────────────────────────────────────────────────────────────────

/**
 * Read-only pill showing the pre-selected date.
 * Reminds the user which day they're adding an event to.
 */
@Composable
private fun DateChip(date: LocalDate) {
    val dayFmt  = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
    val dateFmt = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text  = date.format(dayFmt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text  = date.format(dateFmt),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
