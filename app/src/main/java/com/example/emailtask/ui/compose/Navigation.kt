package com.example.emailtask.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.emailtask.AppApplication
import com.example.emailtask.model.App1ViewModel
import com.example.emailtask.model.App1ViewModelFactory
import com.example.emailtask.ui.compose.screens.ContactDetailsScreen
import com.example.emailtask.ui.compose.screens.ContactsScreen
import com.example.emailtask.ui.compose.screens.EventsScreen
import com.example.emailtask.ui.compose.screens.InstructionsScreen
import com.example.emailtask.ui.compose.screens.ScheduleDetailsScreen
import com.example.emailtask.ui.compose.screens.SchedulesScreen

enum class RootScreens(val route: String) {
    INSTRUCTIONS("instructions_root"),
    CONTACTS("contacts_root"),
    SCHEDULES("schedules_root"),
    EVENTS("events_root")
}

enum class LeafScreens(val route: String) {
    CONTACTS("contacts"),
    CONTACT_DETAILS("contact_details"),
    SCHEDULES("schedules"),
    SCHEDULE_DETAILS("schedule_details")
}

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(viewModel: App1ViewModel) {
    val navigationItems = listOf(
        BottomNavigationItem(
            label = "Instructions",
            icon = Icons.Filled.Info,
            route = RootScreens.INSTRUCTIONS.route
        ),
        BottomNavigationItem(
            label = "Contacts",
            icon = Icons.Filled.AccountCircle,
            route = RootScreens.CONTACTS.route
        ),
        BottomNavigationItem(
            label = "Schedules",
            icon = Icons.Filled.DateRange,
            route = RootScreens.SCHEDULES.route
        ),
        BottomNavigationItem(
            label = "Events",
            icon = Icons.Filled.MailOutline,
            route = RootScreens.EVENTS.route
        ),
    )

    val navController = rememberNavController()
    var navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
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
                    )
                }
            }
        }
    ) { paddingValues ->
        AppNavigation(
            navController = navController,
            paddingValues = paddingValues,
            viewModel = viewModel
        )
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: App1ViewModel
) {
    NavHost(
        navController = navController, startDestination = RootScreens.INSTRUCTIONS.route,
        modifier = Modifier.padding(paddingValues = paddingValues)
    ) {
        composable(RootScreens.INSTRUCTIONS.route) {
            InstructionsScreen()
        }
        navigation(
            route = RootScreens.CONTACTS.route,
            startDestination = LeafScreens.CONTACTS.route
        ) {
            composable(route = LeafScreens.CONTACTS.route) {
                ContactsScreen(navController, viewModel)
            }
            composable(route = LeafScreens.CONTACT_DETAILS.route) {
                ContactDetailsScreen(navController, viewModel)
            }
        }
        navigation(
            route = RootScreens.SCHEDULES.route,
            startDestination = LeafScreens.SCHEDULES.route
        ) {
            composable(route = LeafScreens.SCHEDULES.route) {
                SchedulesScreen(navController, viewModel)
            }
            composable(route = LeafScreens.SCHEDULE_DETAILS.route) {
                ScheduleDetailsScreen(navController, viewModel)
            }
        }
        composable(RootScreens.EVENTS.route) {
            EventsScreen(viewModel)
        }
    }
}