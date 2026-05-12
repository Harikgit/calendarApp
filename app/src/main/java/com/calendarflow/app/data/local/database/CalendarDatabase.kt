package com.calendarflow.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.calendarflow.app.data.local.dao.CalendarEventDao
import com.calendarflow.app.data.local.dao.EventDao
import com.calendarflow.app.data.local.entity.CalendarEvent
import com.calendarflow.app.data.local.entity.EventEntity

/**
 * The single Room database for CalendarFlow.
 *
 * Version history:
 *  v1 — initial schema with "calendar_events" table (CalendarEvent entity)
 *  v2 — added "events" table (EventEntity) with title/description/selectedDate/createdAt
 *
 * Both tables coexist so existing data is preserved during the upgrade.
 * In a real app you would eventually migrate old rows into the new table
 * and drop the legacy one.
 */
@Database(
    entities = [
        CalendarEvent::class,   // legacy table — kept so v1 data isn't lost
        EventEntity::class      // new table introduced in v2
    ],
    version = 2,
    exportSchema = false        // set to true in production to track schema history
)
abstract class CalendarDatabase : RoomDatabase() {

    /** DAO for the legacy calendar_events table. */
    abstract fun calendarEventDao(): CalendarEventDao

    /** DAO for the new events table. */
    abstract fun eventDao(): EventDao

    companion object {

        @Volatile
        private var INSTANCE: CalendarDatabase? = null

        /**
         * Migration from v1 → v2.
         *
         * We only need to CREATE the new "events" table.
         * The existing "calendar_events" table is left untouched.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create the new EventEntity table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `events` (
                        `id`           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title`        TEXT    NOT NULL,
                        `description`  TEXT    NOT NULL DEFAULT '',
                        `selectedDate` INTEGER NOT NULL,
                        `createdAt`    INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        /**
         * Returns the singleton database instance, creating it if needed.
         * [synchronized] prevents two threads from building the DB simultaneously.
         */
        fun getInstance(context: Context): CalendarDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CalendarDatabase::class.java,
                    "calendar_flow_database"
                )
                    .addMigrations(MIGRATION_1_2)   // apply schema upgrade safely
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
