package com.calendarflow.app.ui.addevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calendarflow.app.data.local.entity.EventEntity
import com.calendarflow.app.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for the Add Event screen.
 *
 * Responsibilities:
 *  1. Hold form field values so they survive configuration changes.
 *  2. Validate input before saving.
 *  3. Call [EventRepository.insertEvent] on a background coroutine.
 *  4. Signal success/failure back to the UI via [SaveResult] in [uiState].
 *
 * @param repository  Injected data source.
 * @param selectedDate The date pre-selected on the calendar — passed in via
 *                     the navigation argument so the user doesn't have to pick
 *                     a date again.
 */
class AddEventViewModel(
    private val repository: EventRepository,
    val selectedDate: LocalDate
) : ViewModel() {

    // ── UI state ──────────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow(AddEventUiState())

    /**
     * Exposed as read-only [StateFlow].
     * The screen collects this and recomposes whenever it changes.
     */
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()

    // ── Form field updates ────────────────────────────────────────────────────

    /**
     * Called on every keystroke in the title field.
     * Also clears any existing title error so the red text disappears
     * as soon as the user starts typing.
     */
    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, titleError = null) }
    }

    /** Called on every keystroke in the description field. */
    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    /**
     * Validates the form and, if valid, inserts the event into the database.
     *
     * Flow:
     *  1. Validate — set [AddEventUiState.titleError] and return early if invalid.
     *  2. Set [AddEventUiState.isSaving] = true to disable the button.
     *  3. Call [repository.insertEvent] inside a coroutine.
     *  4. On success → set [SaveResult.Success] (the screen will navigate back).
     *  5. On failure → set [SaveResult.Error] with the exception message.
     */
    fun saveEvent() {
        val currentState = _uiState.value

        // ── Validation ────────────────────────────────────────────────────────
        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title cannot be empty") }
            return
        }

        // ── Persist ───────────────────────────────────────────────────────────
        _uiState.update { it.copy(isSaving = true, titleError = null) }

        viewModelScope.launch {
            try {
                repository.insertEvent(
                    EventEntity(
                        title = currentState.title.trim(),
                        description = currentState.description.trim(),
                        selectedDate = selectedDate.toEpochDay()
                        // createdAt defaults to System.currentTimeMillis() in the entity
                    )
                )
                // Signal success — the screen observes this and navigates back
                _uiState.update { it.copy(isSaving = false, saveResult = SaveResult.Success) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveResult = SaveResult.Error(
                            e.localizedMessage ?: "An unexpected error occurred"
                        )
                    )
                }
            }
        }
    }

    /**
     * Called by the screen after it has reacted to a [SaveResult].
     * Resets the result back to [SaveResult.Idle] so it isn't consumed twice
     * (e.g. after a screen rotation).
     */
    fun onSaveResultConsumed() {
        _uiState.update { it.copy(saveResult = SaveResult.Idle) }
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    /**
     * Custom factory needed because our ViewModel has constructor parameters
     * ([repository] and [selectedDate]) that the default factory can't provide.
     */
    class Factory(
        private val repository: EventRepository,
        private val selectedDate: LocalDate
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddEventViewModel::class.java)) {
                return AddEventViewModel(repository, selectedDate) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
