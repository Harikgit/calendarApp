# CalendarFlow

A modern Android calendar app built with **Kotlin** and **Jetpack Compose**.

## Features

- 📅 Monthly calendar view powered by [Kizitonwose Calendar](https://github.com/kizitonwose/Calendar)
- ➕ Add events to any selected date
- 🗑️ Delete events with a confirmation dialog
- 💾 Local persistence with Room database
- 🌙 Full dark mode support (Material You on Android 12+)
- 🔄 Real-time UI updates via Flow + StateFlow

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Database | Room (SQLite) |
| Async | Kotlin Coroutines + Flow |
| Navigation | Navigation Compose |
| Calendar | Kizitonwose Calendar Compose 2.6.0 |
| Build | Gradle Kotlin DSL + Version Catalog |

## Project Structure

```
app/src/main/java/com/calendarflow/app/
├── data/
│   ├── local/
│   │   ├── dao/          # Room DAOs
│   │   ├── database/     # CalendarDatabase (singleton)
│   │   └── entity/       # EventEntity, CalendarEvent
│   └── repository/       # EventRepository, CalendarRepository
├── navigation/           # NavRoutes, CalendarNavGraph
├── ui/
│   ├── addevent/         # AddEventScreen + ViewModel
│   ├── calendar/         # CalendarScreen + ViewModel + UiState
│   │   └── components/   # CalendarDayCell, EventItem, EmptyState…
│   └── theme/            # Color, Type, Theme
├── CalendarFlowApplication.kt
└── MainActivity.kt
```

## Requirements

- Android Studio Hedgehog or newer
- Android SDK 26+ (minSdk)
- JDK 17

## Getting Started

```bash
git clone https://github.com/Harikgit/calendarApp.git
cd calendarApp
# Open in Android Studio and run on a device or emulator
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

## License

MIT License — see [LICENSE](LICENSE) for details.
