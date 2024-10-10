package com.example.remindme

import java.util.Calendar

data class DateTime (
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
)

fun dateTimeParser(dateTime: Long): DateTime {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = dateTime
    }
    return DateTime(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )
}


