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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class NewsItem(
    val id: String,
    val headline: String,
    val source: String,
    val timeAgo: String,
    val sentiment: String, // "BULLISH", "BEARISH", "NEUTRAL"
    val symbolTag: String? = null
)

val SAMPLE_NEWS = listOf(
    NewsItem(
        "1",
        "Bank Indonesia Holds BI-Rate at 6.00% to Anchor Rupiah Stability and Manage Capital Flows",
        "Bloomberg",
        "12m ago",
        "NEUTRAL",
        "IDX:COMPOSITE"
    ),
    NewsItem(
        "2",
        "Bitcoin Surges Above $94,000 as Institutional Inflows Break All-Time High Records",
        "Reuters",
        "24m ago",
        "BULLISH",
        "BINANCE:BTCUSDT"
    ),
    NewsItem(
        "3",
        "BBCA Records 14.2% YoY Net Profit Growth Led by Corporate Loan Expansion",
        "IDX News",
        "45m ago",
        "BULLISH",
        "IDX:BBCA"
    ),
    NewsItem(
        "4",
        "Crude Oil Drops 1.8% Following OPEC+ Supply Quota Adjustment Speculation",
        "Financial Times",
        "1h ago",
        "BEARISH",
        "CAPITALCOM:OIL_CRUDE"
    ),
    NewsItem(
        "5",
        "US Core PCE Inflation Print Meets Forecast, Markets Price 85% Odds of Rate Cut",
        "CNBC",
        "2h ago",
        "BULLISH",
        "US:SPX"
    )
)

@Composable
fun NewsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Market News & Sentiment",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PiaText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SAMPLE_NEWS) { news ->
                val sentimentColor = when (news.sentiment) {
                    "BULLISH" -> PiaUp
                    "BEARISH" -> PiaDown
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
                            Text(
                                text = news.source,
                                color = PiaTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = " • ${news.timeAgo}",
                                color = PiaTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Sentiment pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(sentimentColor.copy(alpha = 0.15f))
                                .border(0.5.dp, sentimentColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = news.sentiment,
                                color = sentimentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = news.headline,
                        color = PiaText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )

                    if (news.symbolTag != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PiaBorder)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = news.symbolTag,
                                color = PiaTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
