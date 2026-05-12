package com.calendarflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.calendarflow.app.data.repository.EventRepository
import com.calendarflow.app.ui.addevent.AddEventScreen
import com.calendarflow.app.ui.addevent.AddEventViewModel
import com.calendarflow.app.ui.calendar.CalendarScreen
import com.calendarflow.app.ui.calendar.CalendarViewModel
import java.time.LocalDate

/**
 * Root navigation graph for CalendarFlow.
 *
 * Destinations:
 *  - [NavRoutes.Calendar]  — the main calendar view (start destination)
 *  - [NavRoutes.AddEvent]  — the add-event form, receives an epoch day argument
 *
 * Both screens share the same [EventRepository] so they read/write the same
 * Room database and the calendar list refreshes automatically after a save.
 *
 * @param navController  Manages the back stack.
 * @param eventRepository Single repository instance from the Application class.
 */
@Composable
fun CalendarNavGraph(
    navController: NavHostController,
    eventRepository: EventRepository
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Calendar.route
    ) {

        // ── Calendar screen ───────────────────────────────────────────────────
        composable(route = NavRoutes.Calendar.route) {
            val viewModel: CalendarViewModel = viewModel(
                factory = CalendarViewModel.Factory(eventRepository)
            )
            CalendarScreen(
                viewModel = viewModel,
                onAddEvent = { epochDay ->
                    // Navigate to AddEvent, passing the selected date as an argument
                    navController.navigate(NavRoutes.AddEvent.createRoute(epochDay))
                }
            )
        }

        // ── Add Event screen ──────────────────────────────────────────────────
        composable(
            route = NavRoutes.AddEvent.route,
            arguments = listOf(
                navArgument(NavRoutes.AddEvent.ARG_EPOCH_DAY) {
                    type = NavType.LongType   // Room epoch day is a Long
                }
            )
        ) { backStackEntry ->
            // Extract the epoch day from the navigation argument
            val epochDay = backStackEntry.arguments
                ?.getLong(NavRoutes.AddEvent.ARG_EPOCH_DAY)
                ?: LocalDate.now().toEpochDay()

            val selectedDate = LocalDate.ofEpochDay(epochDay)

            val viewModel: AddEventViewModel = viewModel(
                factory = AddEventViewModel.Factory(eventRepository, selectedDate)
            )

            AddEventScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
