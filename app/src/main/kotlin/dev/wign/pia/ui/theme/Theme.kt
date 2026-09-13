package dev.wign.pia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PiaAccent,
    secondary = PiaAccent,
    background = PiaBg,
    surface = PiaCard,
    onPrimary = PiaText,
    onBackground = PiaText,
    onSurface = PiaText
)

@Composable
fun PiaTerminalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
