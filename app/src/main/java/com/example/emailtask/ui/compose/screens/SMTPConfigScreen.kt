package com.example.emailtask.ui.compose.screens

import android.R.attr.host
import android.R.attr.port
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.emailtask.R
import com.example.emailtask.model.AppViewModel
import com.example.emailtask.model.SMTPConfig
import java.util.Properties
import javax.mail.AuthenticationFailedException
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport


@Composable
fun SMTPConfigScreen(navController: NavHostController, viewModel: AppViewModel) {
    val smtpConfig by viewModel.editingSMTPConfig.collectAsState()
    val isValidSMTPConfig by viewModel.isValidSMTPConfig.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            IconButton(
                enabled = isValidSMTPConfig ?: false,
                onClick = {
                    viewModel.updateSMTP(smtpConfig)
                    navController.popBackStack()
                }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_check_24),
                    contentDescription = "Save"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top

        ) {
            OutlinedTextField(
                value = smtpConfig.host,
                onValueChange = { viewModel.setEditingSMTPConfig(smtpConfig.copy(host = it)) },
                label = { Text("Host") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = smtpConfig.port.toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { port ->
                        viewModel.setEditingSMTPConfig(smtpConfig.copy(port = port))
                    }
                },
                label = { Text("Port") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = smtpConfig.email,
                onValueChange = { viewModel.setEditingSMTPConfig(smtpConfig.copy(email = it)) },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = smtpConfig.password,
                onValueChange = { viewModel.setEditingSMTPConfig(smtpConfig.copy(password = it)) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                content = { Text("Test Email") },
                enabled = smtpConfig.host.isNotBlank()
                        && smtpConfig.email.isNotBlank()
                        && smtpConfig.password.isNotBlank()
                        && smtpConfig.port > 0
                        && isValidSMTPConfig == null,
                onClick = {
                    viewModel.testSMTPConfig(smtpConfig)
                }
            )
            if (isValidSMTPConfig == true)
                Text(text = "Success", color = Color.Green)
            if (isValidSMTPConfig == false)
                Text(text = "Failure", color = Color.Red)
        }

    }
}
