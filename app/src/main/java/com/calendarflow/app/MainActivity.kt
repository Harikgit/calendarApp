package com.calendarflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.calendarflow.app.navigation.CalendarNavGraph
import com.calendarflow.app.ui.theme.CalendarFlowTheme

/**
 * The single Activity of the app.
 *
 * All navigation between screens is handled by Navigation Compose inside
 * [CalendarNavGraph]. This Activity just bootstraps the Compose tree.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalendarFlowTheme {
                val navController = rememberNavController()

                // Pull the repository from our Application singleton
                val eventRepository = (application as CalendarFlowApplication).eventRepository

                CalendarNavGraph(
                    navController = navController,
                    eventRepository = eventRepository
                )
            }
        }
    }
}
