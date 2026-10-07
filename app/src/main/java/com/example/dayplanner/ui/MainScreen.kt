package com.example.dayplanner.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dayplanner.ui.calendar.CalendarScreen
import com.example.dayplanner.ui.components.bottomNavItems
import com.example.dayplanner.ui.countdown.CountdownScreen
import com.example.dayplanner.ui.holiday.HolidayDetailScreen
import com.example.dayplanner.ui.holiday.HolidayScreen
import com.example.dayplanner.ui.schedule.ScheduleScreen

@Composable
fun MainScreen() {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 详情页不显示底部导航
    val showBottomBar = currentDestination?.route?.startsWith("holiday/") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "calendar",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("calendar") { CalendarScreen() }
            composable("countdown") { CountdownScreen() }
            composable("holiday") { HolidayScreen(navController) }
            composable("holiday/{holidayId}") { back ->
                HolidayDetailScreen(
                    navController = navController,
                    holidayId = back.arguments?.getString("holidayId") ?: ""
                )
            }
            composable("schedule") { ScheduleScreen() }
        }
    }
}
