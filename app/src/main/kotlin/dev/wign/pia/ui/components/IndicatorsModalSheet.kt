package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.TvAccent
import dev.wign.pia.ui.theme.TvDarkBorder
import dev.wign.pia.ui.theme.TvDarkCard
import dev.wign.pia.ui.theme.TvDarkText
import dev.wign.pia.ui.theme.TvDarkTextMuted
import dev.wign.pia.ui.theme.TvLightBorder
import dev.wign.pia.ui.theme.TvLightCard
import dev.wign.pia.ui.theme.TvLightText
import dev.wign.pia.ui.theme.TvLightTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorsModalSheet(
    sheetState: SheetState,
    isDarkMode: Boolean,
    showEma: Boolean,
    onToggleEma: () -> Unit,
    showRsi: Boolean,
    onToggleRsi: () -> Unit,
    showVolume: Boolean,
    onToggleVolume: () -> Unit,
    showTape: Boolean,
    onToggleTape: () -> Unit,
    showSrLines: Boolean,
    onToggleSrLines: () -> Unit,
    onDismiss: () -> Unit
) {
    val cardColor = if (isDarkMode) TvDarkCard else TvLightCard
    val textColor = if (isDarkMode) TvDarkText else TvLightText
    val mutedColor = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted
    val borderColor = if (isDarkMode) TvDarkBorder else TvLightBorder

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = cardColor,
        contentColor = textColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Indicators",
                        tint = TvAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Indicators & Market Tools",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = mutedColor)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Indicator Rows
            IndicatorRow(
                title = "Exponential Moving Average (EMA 20)",
                subtitle = "Trend smoothing curve overlaid on price candles",
                isChecked = showEma,
                onCheckedChange = { onToggleEma() },
                textColor = textColor,
                mutedColor = mutedColor,
                borderColor = borderColor
            )

            IndicatorRow(
                title = "Relative Strength Index (RSI 14)",
                subtitle = "Momentum oscillator (Overbought > 70, Oversold < 30)",
                isChecked = showRsi,
                onCheckedChange = { onToggleRsi() },
                textColor = textColor,
                mutedColor = mutedColor,
                borderColor = borderColor
            )

            IndicatorRow(
                title = "Volume Histogram",
                subtitle = "Trading volume bars docked at the bottom scale",
                isChecked = showVolume,
                onCheckedChange = { onToggleVolume() },
                textColor = textColor,
                mutedColor = mutedColor,
                borderColor = borderColor
            )

            IndicatorRow(
                title = "24h High & Low S/R Levels",
                subtitle = "Automatic key support (green) and resistance (red) price lines",
                isChecked = showSrLines,
                onCheckedChange = { onToggleSrLines() },
                textColor = textColor,
                mutedColor = mutedColor,
                borderColor = borderColor
            )

            IndicatorRow(
                title = "Live Trades Tape",
                subtitle = "Real-time streaming trade executions and volume flow",
                isChecked = showTape,
                onCheckedChange = { onToggleTape() },
                textColor = textColor,
                mutedColor = mutedColor,
                borderColor = borderColor
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IndicatorRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textColor: androidx.compose.ui.graphics.Color,
    mutedColor: androidx.compose.ui.graphics.Color,
    borderColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = mutedColor, fontSize = 11.sp)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = TvAccent)
        )
    }
}
