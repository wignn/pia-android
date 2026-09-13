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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
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
import dev.wign.pia.ui.theme.TvAccent
import dev.wign.pia.ui.theme.TvDarkBorder
import dev.wign.pia.ui.theme.TvDarkCard
import dev.wign.pia.ui.theme.TvDarkText
import dev.wign.pia.ui.theme.TvDarkTextMuted
import dev.wign.pia.ui.theme.TvDown
import dev.wign.pia.ui.theme.TvLightBorder
import dev.wign.pia.ui.theme.TvLightCard
import dev.wign.pia.ui.theme.TvLightText
import dev.wign.pia.ui.theme.TvLightTextMuted
import dev.wign.pia.ui.theme.TvUp

@Composable
fun TopSymbolBar(
    symbol: String,
    lastPrice: Double,
    changePercent: Double = 0.0,
    currentTimeframe: String,
    chartType: String = "candles",
    isConnected: Boolean = true,
    isDarkMode: Boolean = true,
    onTimeframeSelected: (String) -> Unit,
    onCycleChartType: () -> Unit = {},
    onOpenWatchlist: () -> Unit,
    onOpenIndicators: () -> Unit = {},
    onOpenAlertModal: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val timeframes = listOf("1m", "5m", "15m", "1h", "1D")

    val parts = symbol.split(":")
    val exchange = if (parts.size > 1) parts[0] else "PIA"
    val ticker = if (parts.size > 1) parts[1] else symbol

    val isPositive = changePercent >= 0.0
    val trendColor = if (isPositive) TvUp else TvDown

    val cardColor = if (isDarkMode) TvDarkCard else TvLightCard
    val textColor = if (isDarkMode) TvDarkText else TvLightText
    val mutedColor = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted
    val borderColor = if (isDarkMode) TvDarkBorder else TvLightBorder

    val chartTypeLabel = when (chartType) {
        "line" -> "Line"
        "area" -> "Area"
        "bars" -> "Bars"
        else -> "Candles"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(cardColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Row 1: Left (Symbol, Exchange, Bell) | Right (Price, Change, Theme, fx)
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
                        .background(if (isConnected) TvUp else TvDown)
                )
                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = ticker,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(borderColor.copy(alpha = 0.5f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = exchange,
                        color = mutedColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Symbol",
                    tint = mutedColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set Alert",
                    tint = mutedColor,
                    modifier = Modifier
                        .size(17.dp)
                        .clickable { onOpenAlertModal() }
                )
            }

            // Price, Change & Global Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (lastPrice > 0) formatPrice(lastPrice, symbol) else "--",
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(trendColor.copy(alpha = 0.15f))
                        .border(0.5.dp, trendColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    val sign = if (isPositive) "+" else ""
                    Text(
                        text = "$sign${String.format("%.2f", changePercent)}%",
                        color = trendColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Theme Toggle Icon (Sun / Moon)
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = mutedColor,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onToggleTheme() }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 2: Clean Minimalist Controls (Timeframes, fx Indicator Sheet, Chart Type)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Horizontal Timeframe Selector
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                timeframes.forEach { tf ->
                    val isSelected = tf == currentTimeframe
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) TvAccent else borderColor.copy(alpha = 0.4f))
                            .clickable { onTimeframeSelected(tf) }
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tf,
                            color = if (isSelected) androidx.compose.ui.graphics.Color.White else mutedColor,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Right: Chart Type & fx Indicators Button
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chart Type Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TvAccent.copy(alpha = 0.15f))
                        .border(0.5.dp, TvAccent.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .clickable { onCycleChartType() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = chartTypeLabel,
                        color = TvAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // fx Indicator Sheet Button (MT5 / TradingView style)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(borderColor.copy(alpha = 0.5f))
                        .clickable { onOpenIndicators() }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Indicators",
                            tint = TvAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "fx",
                            color = textColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
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
