package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp

@Composable
fun TopSymbolBar(
    symbol: String,
    lastPrice: Double,
    currentTimeframe: String,
    onTimeframeSelected: (String) -> Unit,
    onOpenWatchlist: () -> Unit,
    showEma: Boolean,
    emaValue: Double?,
    onToggleEma: () -> Unit,
    showRsi: Boolean,
    rsiValue: Double?,
    onToggleRsi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeframes = listOf("1m", "5m", "15m", "1h", "1D")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PiaCard)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Symbol selector button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onOpenWatchlist() }
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = symbol,
                            color = PiaText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Symbol",
                            tint = PiaTextMuted
                        )
                    }
                    Text(
                        text = if (lastPrice > 0) String.format("%.2f", lastPrice) else "--",
                        color = PiaUp,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Timeframe selector
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                timeframes.forEach { tf ->
                    val isSelected = tf == currentTimeframe
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) PiaAccent else PiaBorder)
                            .clickable { onTimeframeSelected(tf) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tf,
                            color = if (isSelected) PiaText else PiaTextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Indicators bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // EMA toggle pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (showEma) PiaAccent else PiaBorder)
                    .clickable { onToggleEma() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                val label = if (showEma && emaValue != null) "EMA20: ${String.format("%.1f", emaValue)}" else "EMA 20"
                Text(
                    text = label,
                    color = if (showEma) PiaText else PiaTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // RSI toggle pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (showRsi) PiaAccent else PiaBorder)
                    .clickable { onToggleRsi() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                val label = if (showRsi && rsiValue != null) "RSI14: ${String.format("%.1f", rsiValue)}" else "RSI 14"
                Text(
                    text = label,
                    color = if (showRsi) PiaText else PiaTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
