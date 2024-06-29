package com.example.emailtask.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.emailtask.R
import androidx.compose.ui.Modifier
import com.example.emailtask.ui.compose.screens.ContactsScreen
import com.example.emailtask.ui.compose.screens.EventsScreen
import com.example.emailtask.ui.compose.screens.InstructionsScreen
import com.example.emailtask.ui.compose.screens.SchedulesScreen

enum class Screens(val route: String) {
    INSTRUCTIONS("instructions"),
    CONTACTS("contacts"),
    SCHEDULES("schedules"),
    EVENTS ("events")
}

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
)

@Composable
fun BottomNavigation() {
    val navController = rememberNavController()
    val navigationItems = listOf(
        BottomNavigationItem(
            label = "Instructions",
            icon = Icons.Filled.Info,
            route = Screens.INSTRUCTIONS.route
        ),
        BottomNavigationItem(
            label = "Contacts",
            icon = Icons.Filled.AccountCircle,
            route = Screens.CONTACTS.route
        ),
        BottomNavigationItem(
            label = "Schedules",
            icon = Icons.Filled.DateRange,
            route = Screens.SCHEDULES.route
        ),
        BottomNavigationItem(
            label = "Events",
            icon = Icons.Filled.MailOutline,
            route = Screens.EVENTS.route
        ),
    )

    var navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navigationItems.forEachIndexed { index, navigationItem ->
                    NavigationBarItem(
                        selected = index == navigationSelectedItem,
                        label = {
                            Text(navigationItem.label)
                        },
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = navigationItem.label
                            )
                        },
                        onClick = {
                            navigationSelectedItem = index
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )                }
            }
        }
    ) { paddingValues ->
        AppNavigation(navController = navController, paddingValues = paddingValues)
    }
}

@Composable
fun AppNavigation(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = Screens.INSTRUCTIONS.route,
        modifier = Modifier.padding(paddingValues = paddingValues)) {
        composable(Screens.INSTRUCTIONS.route) {
            InstructionsScreen()
        }
        composable(Screens.CONTACTS.route) {
            ContactsScreen()
        }
        composable(Screens.SCHEDULES.route) {
            SchedulesScreen()
        }
        composable(Screens.EVENTS.route) {
            EventsScreen()
        }
    }
}