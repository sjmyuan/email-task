package com.example.emailtask.ui.compose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.emailtask.model.App1ViewModel
import com.example.emailtask.model.RecurrenceType
import com.example.emailtask.model.Schedule
import com.example.emailtask.ui.compose.LeafScreens
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SchedulesScreen(navController: NavHostController, viewModel: App1ViewModel = viewModel()) {
    val schedules by viewModel.schedules.collectAsState()
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f)) {
            items(schedules) { item ->
                ScheduleItem(schedule = item,
                    onClick = {
                        viewModel.setEditingSchedule(item)
                        navController.navigate(LeafScreens.SCHEDULE_DETAILS.route)
                    })
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(15.dp),

            onClick = {
                viewModel.setEditingSchedule(
                    Schedule(
                        System.currentTimeMillis(),
                        "",
                        listOf(),
                        listOf(),
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                        RecurrenceType.NOT_REPEAT,
                        ""
                    )
                )
                navController.navigate(LeafScreens.CONTACT_DETAILS.route)
            },
        ) {
            Icon(Icons.Filled.Add, "Add New Schedule")
        }
    }
}

@Composable
fun ScheduleItem(schedule: Schedule, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        overlineContent = { Text(schedule.name) },
        headlineContent = { Text(schedule.message) },
        supportingContent = { Text(schedule.recurrence.description) }
    )
}
