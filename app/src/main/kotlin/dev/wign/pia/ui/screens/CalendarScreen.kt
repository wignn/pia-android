package dev.wign.pia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaDown
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp

data class CalendarEvent(
    val id: String,
    val country: String,
    val time: String,
    val title: String,
    val impact: String, // "HIGH", "MED", "LOW"
    val actual: String,
    val forecast: String,
    val previous: String
)

val SAMPLE_CALENDAR = listOf(
    CalendarEvent("1", "US", "19:30 WIB", "Core CPI (MoM)", "HIGH", "0.3%", "0.3%", "0.2%"),
    CalendarEvent("2", "US", "19:30 WIB", "CPI (YoY)", "HIGH", "2.5%", "2.6%", "2.9%"),
    CalendarEvent("3", "ID", "14:00 WIB", "BI Rate Decision", "HIGH", "6.00%", "6.00%", "6.25%"),
    CalendarEvent("4", "US", "01:00 WIB", "FOMC Rate Decision", "HIGH", "--", "5.00%", "5.25%"),
    CalendarEvent("5", "EU", "16:00 WIB", "ECB President Speaks", "MED", "--", "--", "--"),
    CalendarEvent("6", "US", "19:30 WIB", "Initial Jobless Claims", "MED", "215K", "220K", "228K")
)

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Economic Calendar",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PiaText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SAMPLE_CALENDAR) { event ->
                val impactColor = when (event.impact) {
                    "HIGH" -> PiaDown
                    "MED" -> PiaUp
                    else -> PiaTextMuted
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PiaCard)
                        .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PiaBorder)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = event.country,
                                    color = PiaText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = " ${event.time}",
                                color = PiaTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Impact pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(impactColor.copy(alpha = 0.15f))
                                .border(0.5.dp, impactColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${event.impact} IMPACT",
                                color = impactColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = event.title,
                        color = PiaText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Metrics: Actual, Forecast, Previous
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Actual", color = PiaTextMuted, fontSize = 10.sp)
                            Text(
                                text = event.actual,
                                color = PiaText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column {
                            Text(text = "Forecast", color = PiaTextMuted, fontSize = 10.sp)
                            Text(
                                text = event.forecast,
                                color = PiaTextMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column {
                            Text(text = "Previous", color = PiaTextMuted, fontSize = 10.sp)
                            Text(
                                text = event.previous,
                                color = PiaTextMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
