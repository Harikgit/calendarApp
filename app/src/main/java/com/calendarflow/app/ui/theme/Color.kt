package com.calendarflow.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light scheme base palette ─────────────────────────────────────────────────
val Purple80  = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80    = Color(0xFFEFB8C8)

val Purple40  = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40    = Color(0xFF7D5260)

// ── Surface / background overrides ───────────────────────────────────────────
/** Slightly warm white — used as the screen background in light mode. */
val SurfaceLight = Color(0xFFFFFBFE)

/** Deep dark surface for dark mode. */
val SurfaceDark  = Color(0xFF1C1B1F)

// ── Calendar-specific semantic colours ───────────────────────────────────────

/** Filled circle behind the selected day number. */
val SelectedDayBackground = Purple40

/** Text on top of the selected day circle. */
val SelectedDayText = Color.White

/** Accent colour for today's ring / text when not selected. */
val TodayIndicator = Purple40

/** Muted text for days that belong to the previous / next month. */
val OutMonthText = Color(0xFFBBBBBB)

/** Small event-dot colour (overridden to white when day is selected). */
val EventDotColor = Purple40
