package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaDown
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp

@Composable
fun TopSymbolBar(
    symbol: String,
    lastPrice: Double,
    changePercent: Double = 0.0,
    currentTimeframe: String,
    chartType: String = "candles",
    isConnected: Boolean = true,
    onTimeframeSelected: (String) -> Unit,
    onCycleChartType: () -> Unit = {},
    onOpenWatchlist: () -> Unit,
    showEma: Boolean,
    emaValue: Double?,
    onToggleEma: () -> Unit,
    showRsi: Boolean,
    rsiValue: Double?,
    onToggleRsi: () -> Unit,
    showVolume: Boolean = true,
    onToggleVolume: () -> Unit = {},
    showTape: Boolean = true,
    onToggleTape: () -> Unit = {},
    onOpenAlertModal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val timeframes = listOf("1m", "5m", "15m", "1h", "4h", "1D")

    val parts = symbol.split(":")
    val exchange = if (parts.size > 1) parts[0] else "PIA"
    val ticker = if (parts.size > 1) parts[1] else symbol

    val isPositive = changePercent >= 0.0
    val trendColor = if (isPositive) PiaUp else PiaDown

    val chartTypeLabel = when (chartType) {
        "line" -> "Line"
        "area" -> "Area"
        "bars" -> "Bars"
        else -> "Candles"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PiaCard)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Row 1: Symbol, Exchange badge, Connection Dot, Price, Change Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ticker & Exchange
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onOpenWatchlist() }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) PiaUp else PiaDown)
                )
                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = ticker,
                    color = PiaText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(PiaBorder)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = exchange,
                        color = PiaTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Symbol",
                    tint = PiaTextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set Alert",
                    tint = PiaTextMuted,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onOpenAlertModal() }
                )
            }

            // Price & Change Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (lastPrice > 0) formatPrice(lastPrice, symbol) else "--",
                    color = PiaText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(trendColor.copy(alpha = 0.15f))
                        .border(0.5.dp, trendColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    val sign = if (isPositive) "+" else ""
                    Text(
                        text = "$sign${String.format("%.2f", changePercent)}%",
                        color = trendColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(6.dp))

        // Row 2: Timeframe Pills, Chart Type Switcher & Indicator Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timeframe & Chart Type Selector
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chart Type Cycle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PiaAccent.copy(alpha = 0.25f))
                        .border(0.5.dp, PiaAccent, RoundedCornerShape(4.dp))
                        .clickable { onCycleChartType() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = chartTypeLabel,
                        color = PiaAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                timeframes.forEach { tf ->
                    val isSelected = tf == currentTimeframe
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) PiaAccent else PiaBorder.copy(alpha = 0.5f))
                            .clickable { onTimeframeSelected(tf) }
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tf,
                            color = if (isSelected) PiaText else PiaTextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Indicator Toggles & Tape Toggle
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // VOL Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (showVolume) PiaAccent.copy(alpha = 0.2f) else PiaBorder.copy(alpha = 0.4f))
                        .border(
                            0.5.dp,
                            if (showVolume) PiaAccent else PiaBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onToggleVolume() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "VOL",
                        color = if (showVolume) PiaAccent else PiaTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Live Tape Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (showTape) PiaAccent.copy(alpha = 0.2f) else PiaBorder.copy(alpha = 0.4f))
                        .border(
                            0.5.dp,
                            if (showTape) PiaAccent else PiaBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onToggleTape() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "TAPE",
                        color = if (showTape) PiaAccent else PiaTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // EMA 20
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (showEma) PiaAccent.copy(alpha = 0.2f) else PiaBorder.copy(alpha = 0.4f))
                        .border(
                            0.5.dp,
                            if (showEma) PiaAccent else PiaBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onToggleEma() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    val label = if (showEma && emaValue != null) "EMA: ${formatPrice(emaValue, symbol)}" else "EMA"
                    Text(
                        text = label,
                        color = if (showEma) PiaAccent else PiaTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // RSI 14
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (showRsi) PiaAccent.copy(alpha = 0.2f) else PiaBorder.copy(alpha = 0.4f))
                        .border(
                            0.5.dp,
                            if (showRsi) PiaAccent else PiaBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onToggleRsi() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    val label = if (showRsi && rsiValue != null) "RSI: ${String.format("%.1f", rsiValue)}" else "RSI"
                    Text(
                        text = label,
                        color = if (showRsi) PiaAccent else PiaTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double, symbol: String): String {
    return when {
        symbol.startsWith("IDX:") -> String.format("%,.0f", price)
        price >= 1000.0 -> String.format("%,.2f", price)
        price >= 1.0 -> String.format("%.2f", price)
        else -> String.format("%.4f", price)
    }
}
