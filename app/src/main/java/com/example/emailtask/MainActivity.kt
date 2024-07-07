package com.example.emailtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.emailtask.data.AppDatabase
import com.example.emailtask.ui.compose.BottomNavigation
import com.example.emailtask.ui.compose.theme.EmailTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmailTaskTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    BottomNavigation()
                }
            }
        }

        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "email-task.db")
            .createFromAsset("database/email-task.db")
            .build()
    }
}