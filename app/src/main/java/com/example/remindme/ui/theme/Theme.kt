package com.example.remindme.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remindme.R
import com.example.remindme.Reminder
import com.example.remindme.ReminderDao
import com.example.remindme.ReminderListVM
import com.example.remindme.ReminderManager
import com.example.remindme.ReminderReceiver
import com.example.remindme.dateTimeParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

val MainBlue = Color(33, 150, 243, 255)
val SecondaryRed = Color.Red.copy(0.70f)
var ForcedToUpdate: Reminder? = null

@Composable
fun MainScreen(nav: NavController, vm: ReminderListVM, scope: CoroutineScope, dao: ReminderDao) {
    val remins by vm.remindList
    Scaffold(
        topBar = {
            AppRow(
                content = {
                    AppLogo()
                    AppHeader()
                }
            )
        },
        bottomBar = {
            AppRow(
                isFooter = true,
                padgH = 10.dp, padgV = 10.dp,
                arrag = Arrangement.Center,
                content = {
                    DefActionBtn(act = { nav.navigate("adding_scr") })
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(remins) { rem ->
                Spacer(modifier = Modifier.height(24.dp))
                ReminderCard(rem, nav, vm, scope, dao)
            }
        }
    }
}

@Composable
fun ReminderCard(rem: Reminder, nav: NavController, vm: ReminderListVM, scope: CoroutineScope, dao: ReminderDao) {
    Card(
        modifier = Modifier.fillMaxWidth(0.8f),
        colors = CardDefaults.elevatedCardColors(containerColor = MainBlue),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp)
        ) {
            Column {
                val date = dateTimeParser(rem.dateTime)
                CardText(rem.title)
                Spacer(modifier = Modifier.height(8.dp))
                CardText("Дата: ${date.day}.${date.month}.${date.year}")
                Spacer(modifier = Modifier.height(8.dp))
                var hText = date.hour.toString()
                var mText = date.minute.toString()
                if (hText.length == 1)
                    hText = "0${hText}"
                if (mText.length == 1)
                    mText = "0${mText}"
                CardText("Время: ${hText}:${mText}")
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(
                onClick = {
                    ForcedToUpdate = rem
                    nav.navigate("adding_scr")
                }
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = {
                    vm.remove(rem)
                    scope.launch { dao.delete(rem) }
                    ReminderManager.removeReminder(nav.context, rem.id)
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = SecondaryRed,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun CardText(txt: String)
{
    Text(text = txt, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
}

@Composable
fun AddingReminderScr(nav: NavController, vm: ReminderListVM, scope: CoroutineScope, dao: ReminderDao) {
    var showDP by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    val header: String
    val calendar = Calendar.getInstance()

    if (ForcedToUpdate != null) {
        title = ForcedToUpdate!!.title
        header = "Редактирование напоминания"
        val dt = dateTimeParser(ForcedToUpdate!!.dateTime)
        calendar.set(dt.year, dt.month, dt.day)
        calendar.set(Calendar.HOUR_OF_DAY, dt.hour)
        calendar.set(Calendar.MINUTE, dt.minute)
    } else {
        header = "Добавление напоминания"
    }

    Scaffold(
        topBar = {
            AppRow(
                content = { AppHeader(header, fSz = 24.sp) },
                padgV = 18.dp,
                arrag = Arrangement.Center
            )
        },
        bottomBar = {
            AppRow(
                isFooter = true,
                padgH = 10.dp, padgV = 10.dp,
                arrag = Arrangement.Center,
                content = {
                    DefActionBtn(
                        act = {
                            if (title.isNotBlank()) {
                                val rnew = Reminder(
                                    title = title,
                                    dateTime = calendar.timeInMillis
                                )
                                if (ForcedToUpdate != null) {
                                    scope.launch {
                                        vm.replace(ForcedToUpdate!!.id, rnew)
                                        dao.update(ForcedToUpdate!!, rnew)
                                        ReminderReceiver.ContentText = rnew.title
                                        ReminderManager.updateReminder(
                                            nav.context,
                                            ForcedToUpdate!!.id,
                                            rnew.dateTime
                                        )
                                    }
                                }
                                else {
                                    vm.add(rnew)
                                    scope.launch { dao.insert(rnew) }
                                    ReminderReceiver.ContentText = rnew.title
                                    ReminderManager.setReminder(nav.context, rnew.id, rnew.dateTime)
                                }
                                nav.navigate("main_scr") {
                                    popUpTo("main_scr") {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        icon = Icons.Default.Check
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AppTimePicker(calendar)
            }
            DefTextFld(
                value = title,
                onVal = { title = it },
                tIcon = {
                    IconButton(onClick = {
                        showDP = !showDP
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Date Picker")
                    }
                }
            )
            if (showDP) {
                AppDatePicker(
                    onDismiss = { showDP = false },
                    calendar = calendar
                )
            }
        }
    }
}

@Composable
fun AppLogo() {
    Image(
        modifier = Modifier.size(64.dp),
        painter = painterResource(R.drawable.logo),
        contentDescription = "App Logo",
        colorFilter = ColorFilter.tint(Color.White)
    )
}

@Composable
fun AppHeader(txt: String = "RemindMe", fSz: TextUnit = 32.sp) {
    Text(
        txt,
        modifier = Modifier.padding(horizontal = 12.dp),
        fontSize = fSz,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun DefActionBtn(act: () -> Unit = {}, icon: ImageVector = Icons.Default.Add) {
    FloatingActionButton(
        onClick = act,
        containerColor = Color.White,
        contentColor = MainBlue,
        shape = CircleShape,
    ) {
        Icon(icon, contentDescription = icon.name)
    }
}

@Composable
fun DefTextFld(value: String, onVal: (String) -> Unit = {}, tIcon: @Composable (() -> Unit) = {}) {
    TextField(
        modifier = Modifier.fillMaxWidth(0.8f),
        value = value,
        textStyle = TextStyle(fontSize = 20.sp),
        onValueChange = onVal,
        trailingIcon = tIcon,
        placeholder = { Text("Название", color = Color.Black.copy(0.3f)) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MainBlue,
            cursorColor = MainBlue
        )
    )
}

@Composable
fun AppRow(
    content: @Composable (RowScope.() -> Unit) = {},
    padgV: Dp = 12.dp, padgH: Dp = 12.dp,
    isFooter: Boolean = false,
    arrag: Arrangement.Horizontal = Arrangement.Start,
    alig: Alignment.Vertical = Alignment.CenterVertically
) {
    if (!isFooter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(MainBlue)
                .padding(vertical = padgV, horizontal = padgH),
            verticalAlignment = alig,
            horizontalArrangement = arrag,
            content = content
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(MainBlue)
                .padding(vertical = padgV, horizontal = padgH),
            verticalAlignment = alig,
            horizontalArrangement = arrag,
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePicker(calendar: Calendar) {
    val tps = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true,
    )
    calendar.set(Calendar.HOUR_OF_DAY, tps.hour)
    calendar.set(Calendar.MINUTE, tps.minute)
    TimePicker(
        modifier = Modifier.padding(vertical = 35.dp),
        state = tps,
        colors = TimePickerDefaults.colors(
            timeSelectorSelectedContainerColor = MainBlue,
            timeSelectorSelectedContentColor = Color.White,
            selectorColor = MainBlue,
            containerColor = MainBlue,
        ),
        layoutType = TimePickerDefaults.layoutType()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePicker(calendar: Calendar, onDismiss: () -> Unit = {}) {
    val dps = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                calendar.timeInMillis = dps.selectedDateMillis!!
                onDismiss()
            }) {
                Text("OK", color = MainBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = MainBlue)
            }
        }
    ) {
        DatePicker(
            state = dps,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MainBlue,
                todayDateBorderColor = MainBlue,
                todayContentColor = Color.Black,
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MainBlue,
                    focusedLabelColor = MainBlue,
                    cursorColor = MainBlue
                )
            )
        )
    }
}