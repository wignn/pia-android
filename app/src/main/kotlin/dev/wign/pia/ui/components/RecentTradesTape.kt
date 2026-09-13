package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.data.MarketTick
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
fun RecentTradesTape(
    trades: List<MarketTick>,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isDarkMode) TvDarkCard else TvLightCard
    val textColor = if (isDarkMode) TvDarkText else TvLightText
    val mutedColor = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted
    val borderColor = if (isDarkMode) TvDarkBorder else TvLightBorder

    // Compact Sleek Micro-Tape (24dp height, zero intrusion on chart)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(cardColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TAPE",
            color = mutedColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(12.dp)
                .background(borderColor)
        )
        Spacer(modifier = Modifier.width(6.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            trades.take(15).forEach { tick ->
                val isUp = tick.price >= (trades.getOrNull(1)?.price ?: tick.price)
                val dotColor = if (isUp) TvUp else TvDown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Text(
                        text = if (tick.price >= 1000) String.format("%,.1f", tick.price) else String.format("%.2f", tick.price),
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (tick.volume > 0) {
                        Text(
                            text = "(${String.format("%.2f", tick.volume)})",
                            color = mutedColor,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
