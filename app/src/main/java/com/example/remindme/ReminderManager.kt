package com.example.remindme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class ReminderManager {
    companion object {
        fun setReminder(context: Context, remId: Int, whenTime: Long) {
            val intent = Intent(context, ReminderReceiver::class.java).apply { putExtra("Rem_ID", remId) }
            val pin = PendingIntent.getBroadcast(context, remId, intent, PendingIntent.FLAG_IMMUTABLE)
            val almr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            almr.set(AlarmManager.RTC_WAKEUP, whenTime, pin)
        }

        fun removeReminder(context: Context, remId: Int) {
            val intent = Intent(context, ReminderReceiver::class.java)
            val pin = PendingIntent.getBroadcast(context, remId, intent, PendingIntent.FLAG_IMMUTABLE)
            val almr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            almr.cancel(pin)
        }

        fun updateReminder(context: Context, remId: Int, newTime: Long) {
            removeReminder(context, remId)
            setReminder(context, remId, newTime)
        }
    }
}
