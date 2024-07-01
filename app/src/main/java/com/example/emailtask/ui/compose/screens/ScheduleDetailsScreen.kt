package com.example.emailtask.ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.emailtask.model.App1ViewModel
import com.example.emailtask.model.Contact
import com.example.emailtask.model.RecurrenceType
import com.example.emailtask.ui.compose.LeafScreens
import com.example.emailtask.ui.compose.utils.ScheduleReceiversDialog
import com.example.emailtask.ui.compose.utils.TimePickerDialog
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailsScreen(
    navController: NavHostController,
    viewModel: App1ViewModel = viewModel()
) {
    val editingSchedule by viewModel.editingSchedule.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

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

    var showReceiversEditor by remember { mutableStateOf(false) }
    var receivers by remember {
        mutableStateOf(
            editingSchedule?.receivers?.mapNotNull { contacts.find { contact -> contact.id == it } }
                ?: listOf()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.Gray)
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
                { },
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
                { },
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray)
            ) {
                LazyColumn {
                    items(receivers) { item ->
                        ListItem(headlineContent = { Text(item.name) })
                    }
                }
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = {
                        showReceiversEditor = true
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
                        viewModel.updateSchedule(
                            selectedDate?.let {
                                schedule.copy(sentTime = LocalDateTime(it, selectedTime))
                            } ?: schedule
                        )
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

    if (showReceiversEditor) {
        ScheduleReceiversDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReceiversEditor = false
                        viewModel.setEditingSchedule(
                            editingSchedule
                                ?.copy(receivers = receivers.map { it.id })
                        )
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showReceiversEditor = false
                        receivers =
                            editingSchedule?.receivers?.mapNotNull { contacts.find { contact -> contact.id == it } }
                                ?: listOf()
                    }
                ) { Text("Cancel") }
            }
        )
        {
            ReceiverPicker(
                Modifier.width(256.dp),
                contacts,
                receivers
            ) { checked, receiver ->
                receivers = if (checked) receivers + receiver
                else receivers.filterNot { it.id == receiver.id }
            }
        }
    }

}

@Composable
fun ReceiverPicker(
    modifier: Modifier,
    contacts: List<Contact>,
    receivers: List<Contact>,
    oncChange: (checked: Boolean, receiver: Contact) -> Unit
) {
    Column(modifier = modifier) {
        contacts.map { item ->
            ContactPickerItem(name = item.name,
                receivers.find { it.id == item.id } != null,
                onCheck = { checked ->
                    oncChange(checked, item)
                })
        }
    }
}

@Composable
fun ContactPickerItem(name: String, checked: Boolean, onCheck: (check: Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(name) },
        trailingContent = { Checkbox(checked = checked, onCheckedChange = onCheck) }
    )
}
