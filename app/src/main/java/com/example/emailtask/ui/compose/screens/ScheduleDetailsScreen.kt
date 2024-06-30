package com.example.emailtask.ui.compose.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.emailtask.model.App1ViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.emailtask.model.RecurrenceType
import com.example.emailtask.model.Schedule
import com.example.emailtask.ui.compose.LeafScreens
import com.example.emailtask.ui.compose.utils.TimePickerDialog
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailsScreen(
    navController: NavHostController,
    viewModel: App1ViewModel = viewModel()
) {
    val editingSchedule by viewModel.editingSchedule.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val receivers =
        editingSchedule?.receivers?.mapNotNull { contacts.find { contact -> contact.id == it } }
            ?: listOf()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = editingSchedule?.sentTime?.toInstant(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds()
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = datePickerState.selectedDateMillis?.let { Instant.fromEpochMilliseconds(it) }
        ?.toLocalDateTime(TimeZone.currentSystemDefault())?.date

    val timePickerState = rememberTimePickerState(
        initialHour = editingSchedule?.sentTime?.hour ?: 0,
        initialMinute = editingSchedule?.sentTime?.minute ?: 0,
        is24Hour = false
    )
    var showTimePicker by remember { mutableStateOf(false) }
    val selectedTime = LocalTime(timePickerState.hour, timePickerState.minute)

    var showRecurrenceTypeList by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.Start),
            onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                editingSchedule?.name.orEmpty(),
                { name -> viewModel.setEditingSchedule(editingSchedule?.copy(name = name)) },
                label = { Text(text = "Name") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                editingSchedule?.message.orEmpty(),
                { message -> viewModel.setEditingSchedule(editingSchedule?.copy(message = message)) },
                label = { Text(text = "Message") },
                singleLine = false,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                selectedDate?.format(LocalDate.Formats.ISO).orEmpty(),
                { value ->
                    editingSchedule?.sentTime?.time?.let {
                        val date = LocalDate.Formats.ISO.parse(value)
                        viewModel.setEditingSchedule(
                            editingSchedule?.copy(
                                sentTime = LocalDateTime(
                                    date,
                                    it
                                )
                            )
                        )
                    }
                },
                label = { Text(text = "Date") },
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .clickable(enabled = true) {
                        showDatePicker = true
                    }
                    .padding(8.dp)
            )

            OutlinedTextField(
                selectedTime.format(LocalTime.Formats.ISO),
                { value ->
                    editingSchedule?.sentTime?.date?.let {
                        val time = LocalTime.Formats.ISO.parse(value)
                        viewModel.setEditingSchedule(
                            editingSchedule?.copy(
                                sentTime = LocalDateTime(
                                    it,
                                    time
                                )
                            )
                        )
                    }
                },
                label = { Text(text = "Time") },
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .clickable(enabled = true) {
                        showTimePicker = true
                    }
                    .padding(8.dp)
            )

            Column {
                OutlinedTextField(
                    editingSchedule?.recurrence?.description.orEmpty(),
                    {},
                    label = { Text(text = "Repeat") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .clickable(enabled = true) {
                            showRecurrenceTypeList = !showRecurrenceTypeList
                        }
                        .padding(8.dp)
                )

                DropdownMenu(
                    expanded = showRecurrenceTypeList,
                    onDismissRequest = { showRecurrenceTypeList = false }) {
                    Column {
                        RecurrenceType.entries.map {
                            DropdownMenuItem(text = { Text(it.description) }, onClick = {
                                viewModel.setEditingSchedule(editingSchedule?.copy(recurrence = it))
                                showRecurrenceTypeList = false
                            })
                        }
                    }
                }
            }

            Column {
                LazyColumn {
                    items(receivers) { item ->
                        ListItem(headlineContent = { Text(item.name) })
                    }
                }
                Button(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .padding(4.dp),

                    onClick = {
                    },
                ) {
                    Icon(Icons.Filled.Edit, "Edit Schedule Receivers")
                }
            }

            Button(
                enabled = editingSchedule?.let {
                    it.name.isNotBlank()
                            && it.message.isNotBlank()
                            && it.receivers.isNotEmpty()
                } == true,
                onClick = {
                    editingSchedule?.let { schedule ->
                        viewModel.updateSchedule(schedule)
                    }
                    navController.popBackStack()
                }, content = { Text("Save") })
        }

    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) { Text("Cancel") }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) { Text("Cancel") }
            }
        )
        {
            TimePicker(state = timePickerState)
        }
    }

}

@Composable
fun DropdownList(
    itemList: List<String>,
    selectedIndex: Int,
    modifier: Modifier,
    onItemClick: (Int) -> Unit
) {

    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = modifier
                .clickable { showDropdown = true },
            contentAlignment = Alignment.Center
        ) {
            Text(text = itemList[selectedIndex], modifier = Modifier.padding(3.dp))
        }

        Box() {
            if (showDropdown) {
                Popup(
                    alignment = Alignment.TopCenter,
                    properties = PopupProperties(
                        excludeFromSystemGesture = true,
                    ),
                    onDismissRequest = { showDropdown = false }
                ) {
                    Column(
                        modifier = modifier
                            .heightIn(max = 90.dp)
                            .verticalScroll(state = scrollState)
                            .border(width = 1.dp, color = Color.Gray),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        itemList.onEachIndexed { index, item ->
                            if (index != 0) {
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemClick(index)
                                        showDropdown = !showDropdown
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item)
                            }
                        }
                    }
                }
            }
        }
    }

}