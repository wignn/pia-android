package dev.wign.pia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.TvAccent
import dev.wign.pia.ui.theme.TvDarkBg
import dev.wign.pia.ui.theme.TvDarkBorder
import dev.wign.pia.ui.theme.TvDarkText
import dev.wign.pia.ui.theme.TvDarkTextMuted
import dev.wign.pia.ui.theme.TvLightBg
import dev.wign.pia.ui.theme.TvLightBorder
import dev.wign.pia.ui.theme.TvLightText
import dev.wign.pia.ui.theme.TvLightTextMuted

@Composable
fun MarketsScreen(
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf("heatmap") } // "heatmap", "intel", "calendar"

    val bg = if (isDarkMode) TvDarkBg else TvLightBg
    val textPrimary = if (isDarkMode) TvDarkText else TvLightText
    val textMuted = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted
    val borderColor = if (isDarkMode) TvDarkBorder else TvLightBorder

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        // Section Segmented Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val sections = listOf(
                Pair("heatmap", "Heatmap"),
                Pair("intel", "AI Intel"),
                Pair("calendar", "Calendar")
            )

            sections.forEach { (id, label) ->
                val isSelected = selectedSection == id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) TvAccent else borderColor.copy(alpha = 0.4f))
                        .clickable { selectedSection = id }
                        .padding(vertical = 7.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) androidx.compose.ui.graphics.Color.White else textMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Section Content
        when (selectedSection) {
            "heatmap" -> HeatmapScreen(isDarkMode = isDarkMode)
            "intel" -> IntelScreen()
            "calendar" -> CalendarScreen()
        }
    }
}
