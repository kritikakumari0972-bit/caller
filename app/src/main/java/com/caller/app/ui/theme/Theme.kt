package com.caller.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CallerPurple = Color(0xFF6558D3)
val CallerPurpleDark = Color(0xFF4D42B5)
val CallerLavender = Color(0xFFEAE8FF)
val CallerBackground = Color(0xFFF7F7FC)
val CallerSurface = Color(0xFFFFFFFF)
val CallerText = Color(0xFF1B1B2A)
val CallerMuted = Color(0xFF77778A)
val CallerGreen = Color(0xFF16845B)
val CallerGreenSoft = Color(0xFFE3F5ED)
val CallerRed = Color(0xFFB83E50)
val CallerRedSoft = Color(0xFFFFE9ED)

private val CallerLightColorScheme = lightColorScheme(
    primary = CallerPurple,
    onPrimary = CallerSurface,
    secondary = CallerPurpleDark,
    onSecondary = CallerSurface,
    background = CallerBackground,
    onBackground = CallerText,
    surface = CallerSurface,
    onSurface = CallerText,
    error = CallerRed,
    onError = CallerSurface
)

@Composable
fun CallerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CallerLightColorScheme,
        content = content
    )
}
