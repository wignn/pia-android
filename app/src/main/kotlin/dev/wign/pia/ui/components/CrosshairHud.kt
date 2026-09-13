package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.data.Candle
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaDown
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CrosshairHud(
    candle: Candle?,
    modifier: Modifier = Modifier
) {
    if (candle == null) return

    val isUp = candle.close >= candle.open
    val color = if (isUp) PiaUp else PiaDown
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeFormatted = sdf.format(Date(candle.time * 1000))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(PiaBg)
            .padding(horizontal = 12.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeFormatted,
            color = PiaTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "O:${formatVal(candle.open)}",
            color = PiaText,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "H:${formatVal(candle.high)}",
            color = PiaUp,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "L:${formatVal(candle.low)}",
            color = PiaDown,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "C:${formatVal(candle.close)}",
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "V:${formatVol(candle.volume)}",
            color = PiaTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

private fun formatVal(v: Double): String {
    return if (v >= 1000.0) String.format("%,.1f", v) else String.format("%.2f", v)
}

private fun formatVol(v: Double): String {
    return when {
        v >= 1_000_000 -> String.format("%.1fM", v / 1_000_000)
        v >= 1_000 -> String.format("%.1fK", v / 1_000)
        v > 0 -> String.format("%.0f", v)
        else -> "--"
    }
}
