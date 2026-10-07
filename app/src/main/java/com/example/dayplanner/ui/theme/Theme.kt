package com.example.dayplanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF3D5AFE),
    secondary = Color(0xFF00BFA5),
    tertiary = Color(0xFFF50057),
    background = Color(0xFFF6F7FB)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8C9EFF),
    secondary = Color(0xFF1DE9B6),
    tertiary = Color(0xFFFF4081)
)

@Composable
fun DayPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
