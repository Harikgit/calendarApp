package com.calendarflow.app.ui.addevent

/**
 * Immutable UI state for the Add Event screen.
 *
 * Every field the form needs is captured here so the composable is a
 * pure function of this object.
 *
 * @param title           Current value of the title text field.
 * @param description     Current value of the description text field.
 * @param titleError      Non-null error message when title validation fails.
 * @param isSaving        True while the insert coroutine is running — used to
 *                        disable the Save button and show a loading indicator.
 * @param saveResult      Signals the outcome of a save attempt so the screen
 *                        can react (navigate back on success, show error on failure).
 */
data class AddEventUiState(
    val title: String = "",
    val description: String = "",
    val titleError: String? = null,
    val isSaving: Boolean = false,
    val saveResult: SaveResult = SaveResult.Idle
)

/**
 * Represents the outcome of a save operation.
 *
 * Using a sealed class (instead of a Boolean) lets us carry extra data
 * with each state — e.g. an error message — without adding nullable fields.
 */
sealed class SaveResult {
    /** No save has been attempted yet, or the result has been consumed. */
    data object Idle : SaveResult()

    /** The event was inserted successfully. */
    data object Success : SaveResult()

    /**
     * The insert failed.
     * @param message Human-readable reason shown in a Snackbar.
     */
    data class Error(val message: String) : SaveResult()
}
