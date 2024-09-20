package com.example.emailtask.ui.compose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.emailtask.model.App1ViewModel
import com.example.emailtask.model.Event
import com.example.emailtask.model.Schedule
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

@Composable
fun EventsScreen(viewModel: App1ViewModel = viewModel()) {
    val schedules by viewModel.schedules.collectAsState()
    val events = schedules.flatMap { schedule ->
        schedule.events.map { event ->
            Pair(
                schedule.name,
                event
            )
        }
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f)) {
            items(events) { item ->
                EventItem(item)
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun EventItem(event: Pair<String, Event>) {
    ListItem(
        leadingContent = { Text(event.second.status.name) },
        overlineContent = { Text(event.first) },
        headlineContent = {
            Text(
                event.second.message,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = { Text(event.second.receiverName) },
        trailingContent = { Text(event.second.sentTime.format(LocalDateTime.Formats.ISO)) }
    )
}
