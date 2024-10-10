package com.example.remindme

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,
    val dateTime: Long
)
