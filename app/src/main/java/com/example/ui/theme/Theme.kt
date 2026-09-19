package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MigColorScheme = lightColorScheme(
    primary = MigBlue,
    onPrimary = Color.White,
    primaryContainer = MigDarkBlue,
    onPrimaryContainer = Color.White,
    secondary = MigOrange,
    onSecondary = Color.White,
    tertiary = MigYellow,
    background = Color(0xFFE8ECEF),
    surface = Color.White,
    onBackground = MigTextDark,
    onSurface = MigTextDark,
    surfaceVariant = Color(0xFFF0F4F8),
    onSurfaceVariant = MigTextMuted
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MigColorScheme,
        typography = Typography,
        content = content
    )
}
