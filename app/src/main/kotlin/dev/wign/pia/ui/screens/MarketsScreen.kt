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
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted

@Composable
fun MarketsScreen(modifier: Modifier = Modifier) {
    var selectedSection by remember { mutableStateOf("heatmap") } // "heatmap", "intel", "calendar"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBg)
    ) {
        // Section Segmented Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
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
                        .background(if (isSelected) PiaAccent else PiaBorder.copy(alpha = 0.4f))
                        .clickable { selectedSection = id }
                        .padding(vertical = 8.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) PiaText else PiaTextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Section Content
        when (selectedSection) {
            "heatmap" -> HeatmapScreen()
            "intel" -> IntelScreen()
            "calendar" -> CalendarScreen()
        }
    }
}
