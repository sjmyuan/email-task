package com.example.emailtask.ui.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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

@Composable
fun ContactDetailsScreen(navController: NavHostController, viewModel: App1ViewModel = viewModel()) {
    val editingContact by viewModel.editingContact.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
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
                editingContact?.name.orEmpty(),
                { name -> viewModel.setEditingContact(editingContact?.copy(name = name)) },
                label = { Text(text = "Name") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                editingContact?.email.orEmpty(),
                { email -> viewModel.setEditingContact(editingContact?.copy(email = email)) },
                label = { Text(text = "Email") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                editingContact?.mobile.orEmpty(),
                { mobile -> viewModel.setEditingContact(editingContact?.copy(mobile = mobile)) },
                label = { Text(text = "Mobile") },
                modifier = Modifier.padding(8.dp)
            )

            Button(
                enabled = editingContact?.let { it.mobile.isNotBlank() && it.name.isNotBlank() && it.email.isNotBlank() } == true,
                onClick = {
                    editingContact?.let { contact ->
                        viewModel.updateContact(contact)
                    }
                    navController.popBackStack()
                }, content = { Text("Save") })
        }

    }

}