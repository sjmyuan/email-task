package com.example.emailtask

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.emailtask.model.AppViewModel
import com.example.emailtask.model.App1ViewModelFactory
import com.example.emailtask.ui.compose.BottomNavigation
import com.example.emailtask.ui.compose.theme.EmailTaskTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appApplication = application as AppApplication
        val appViewModel: AppViewModel by viewModels {
            App1ViewModelFactory(
                appApplication.contactRepository,
                appApplication.scheduleRepository,
                appApplication.settingRepository
            )
        }

        val intent = Intent(applicationContext, MainService::class.java)
        startForegroundService(intent)

        setContent {
            EmailTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    BottomNavigation(appViewModel)
                }
            }
        }
    }
}
