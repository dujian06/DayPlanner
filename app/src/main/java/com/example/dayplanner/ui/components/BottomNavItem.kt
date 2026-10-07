package com.example.dayplanner.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("calendar", "日历", Icons.Default.DateRange),
    BottomNavItem("countdown", "倒数本", Icons.Default.List),
    BottomNavItem("holiday", "节假日", Icons.Default.Star),
    BottomNavItem("schedule", "小工具", Icons.Default.Build)
)
