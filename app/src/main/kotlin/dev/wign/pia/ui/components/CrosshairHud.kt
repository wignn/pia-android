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
import dev.wign.pia.ui.theme.PiaCard
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
            .background(PiaCard)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeFormatted,
            color = PiaTextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "O:${String.format("%.1f", candle.open)}",
            color = PiaText,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "H:${String.format("%.1f", candle.high)}",
            color = PiaUp,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "L:${String.format("%.1f", candle.low)}",
            color = PiaDown,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "C:${String.format("%.1f", candle.close)}",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "V:${String.format("%.0f", candle.volume)}",
            color = PiaTextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
