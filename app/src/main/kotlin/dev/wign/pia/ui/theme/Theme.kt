package dev.wign.pia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TvAccent,
    secondary = TvAccent,
    background = TvDarkBg,
    surface = TvDarkCard,
    onPrimary = TvDarkText,
    onBackground = TvDarkText,
    onSurface = TvDarkText
)

private val LightColorScheme = lightColorScheme(
    primary = TvAccent,
    secondary = TvAccent,
    background = TvLightBg,
    surface = TvLightCard,
    onPrimary = TvLightText,
    onBackground = TvLightText,
    onSurface = TvLightText
)

@Composable
fun PiaTerminalTheme(
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkMode) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
