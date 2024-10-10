package com.example.remindme

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1)
abstract class ReminderDB : RoomDatabase() {
    companion object {
        @Volatile
        private var _instance: ReminderDB? = null

        fun instance(context: Context): ReminderDB {
            return _instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDB::class.java,
                    "reminders_db"
                ).build()
                _instance = instance
                instance
            }
        }
    }

    abstract fun reminderDao(): ReminderDao
}