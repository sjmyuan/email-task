package com.example.emailtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.emailtask.model.App1ViewModel
import com.example.emailtask.model.App1ViewModelFactory
import com.example.emailtask.ui.compose.BottomNavigation
import com.example.emailtask.ui.compose.theme.EmailTaskTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appApplication = application as AppApplication
        val app1ViewModel: App1ViewModel by viewModels {
            App1ViewModelFactory(
                appApplication.contactRepository,
                appApplication.scheduleRepository
            )
        }

        val eventGeneratorRequest =
            PeriodicWorkRequestBuilder<EventGenerator>(1, TimeUnit.MINUTES).build()

        WorkManager.getInstance(applicationContext).enqueue(eventGeneratorRequest)

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