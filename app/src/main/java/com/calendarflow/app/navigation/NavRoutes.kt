package com.calendarflow.app.navigation

/**
 * Type-safe navigation route definitions.
 *
 * Using a sealed class means a typo in a route name is a compile error,
 * not a silent runtime crash.
 */
sealed class NavRoutes(val route: String) {

    /** Main calendar view. */
    data object Calendar : NavRoutes("calendar")

    /**
     * Add Event screen.
     *
     * Requires an [epochDay] argument so the screen knows which date to
     * pre-fill without the user having to pick a date again.
     *
     * Full route template:  "add_event/{epochDay}"
     * Navigating example:   navController.navigate("add_event/19854")
     *
     * Use [createRoute] to build the concrete route string safely.
     */
    data object AddEvent : NavRoutes("add_event/{epochDay}") {
        const val ARG_EPOCH_DAY = "epochDay"

        /** Build the concrete navigation route for a specific epoch day. */
        fun createRoute(epochDay: Long) = "add_event/$epochDay"
    }
}
