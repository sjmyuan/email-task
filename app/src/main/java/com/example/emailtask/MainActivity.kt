package com.example.emailtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.emailtask.model.App1ViewModel
import com.example.emailtask.model.App1ViewModelFactory
import com.example.emailtask.ui.compose.BottomNavigation
import com.example.emailtask.ui.compose.theme.EmailTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appApplication = application as AppApplication
        val app1ViewModel: App1ViewModel by viewModels {
            App1ViewModelFactory(appApplication.contactRepository, appApplication.scheduleRepository)
        }
        setContent {
            EmailTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomNavigation(app1ViewModel)
                }
            }
        }
    }
}