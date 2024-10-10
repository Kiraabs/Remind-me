package com.example.remindme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remindme.ui.theme.AddingReminderScr
import com.example.remindme.ui.theme.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dao = ReminderDB.instance(applicationContext).reminderDao()
            val nav = rememberNavController()
            val vm: ReminderListVM = viewModel()
            val scope = rememberCoroutineScope()
            LaunchedEffect(Unit) { vm.copyFrom(dao.getReminders()) }
            NavHost(
                navController = nav,
                startDestination = "main_scr"
            ) {
                composable("main_scr") {
                    MainScreen(nav, vm, scope, dao)
                }
                composable("adding_scr") {
                    AddingReminderScr(nav, vm, scope, dao)
                }
            }
        }
    }
}