package com.example.emailtask.ui.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.emailtask.model.App1ViewModel
import com.example.emailtask.model.Contact
import com.example.emailtask.ui.compose.LeafScreens

@Composable
fun ContactsScreen(navController: NavHostController, viewModel: App1ViewModel = viewModel()) {
    val contacts by viewModel.contacts.collectAsState()
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f)) {
            items(contacts) { item ->
                ContactItem(name = item.name,
                    onEdit = {
                        viewModel.setEditingContact(item)
                        navController.navigate(LeafScreens.CONTACT_DETAILS.route)
                    }, onDelete = {
                        viewModel.deleteContact(item)
                    })
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(15.dp),

            onClick = {
                viewModel.setEditingContact(Contact(System.currentTimeMillis(), "", "", ""))
                navController.navigate(LeafScreens.CONTACT_DETAILS.route)
            },
        ) {
            Icon(Icons.Filled.Add, "Add New Contact")
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactItem(name: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showDropDownList by remember { mutableStateOf(false) }
    Box() {
        ListItem(
            modifier = Modifier.combinedClickable(
                onClick = onEdit,
                onLongClick = { showDropDownList = true }),
            headlineContent = { Text(name) },
        )
        DropdownMenu(
            modifier = Modifier.align(Alignment.Center),
            expanded = showDropDownList,
            onDismissRequest = { showDropDownList = false }) {
            Column {
                DropdownMenuItem(text = { Text("Delete") }, onClick = {
                    onDelete()
                    showDropDownList = false
                })
            }
        }
    }
}
