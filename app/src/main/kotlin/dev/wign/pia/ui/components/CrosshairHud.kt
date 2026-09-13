package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.data.Candle
import dev.wign.pia.ui.theme.TvDarkBg
import dev.wign.pia.ui.theme.TvDarkText
import dev.wign.pia.ui.theme.TvDarkTextMuted
import dev.wign.pia.ui.theme.TvDown
import dev.wign.pia.ui.theme.TvLightBg
import dev.wign.pia.ui.theme.TvLightText
import dev.wign.pia.ui.theme.TvLightTextMuted
import dev.wign.pia.ui.theme.TvUp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CrosshairHud(
    candle: Candle?,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (candle == null) return

    val isUp = candle.close >= candle.open
    val color = if (isUp) TvUp else TvDown
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeFormatted = sdf.format(Date(candle.time * 1000))

    val bg = if (isDarkMode) TvDarkBg else TvLightBg
    val textPrimary = if (isDarkMode) TvDarkText else TvLightText
    val textMuted = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted

    // TradingView Minimalist HUD Row (20dp height, zero vertical waste)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(bg)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeFormatted,
            color = textMuted,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "O ${formatVal(candle.open)}",
            color = textPrimary,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "H ${formatVal(candle.high)}",
            color = TvUp,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "L ${formatVal(candle.low)}",
            color = TvDown,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "C ${formatVal(candle.close)}",
            color = color,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "V ${formatVol(candle.volume)}",
            color = textMuted,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

private fun formatVal(value: Double): String {
    return when {
        value >= 1000.0 -> String.format("%,.1f", value)
        value >= 1.0 -> String.format("%.2f", value)
        else -> String.format("%.4f", value)
    }
}

private fun formatVol(vol: Double): String {
    return when {
        vol >= 1_000_000 -> String.format("%.1fM", vol / 1_000_000)
        vol >= 1_000 -> String.format("%.1fK", vol / 1_000)
        else -> String.format("%.0f", vol)
    }
}
