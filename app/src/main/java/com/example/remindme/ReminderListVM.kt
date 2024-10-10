package com.example.remindme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ReminderListVM : ViewModel() {
    private val _reminds = mutableStateOf(listOf<Reminder>())
    val remindList: State<List<Reminder>> = _reminds

    fun copyFrom(list: List<Reminder>) {
        list.forEach{_reminds.value += it}
    }

    fun add(reminder: Reminder) {
        _reminds.value += reminder
    }

    fun remove(reminder: Reminder) {
        _reminds.value -= reminder
    }

    fun replace(oldId: Int, newRem: Reminder) {
        _reminds.value -= _reminds.value.find { it.id == oldId }!!
        newRem.id = oldId
        _reminds.value += newRem
    }
}