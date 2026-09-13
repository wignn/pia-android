package dev.wign.pia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.data.PiaApiClient
import dev.wign.pia.data.SocialPostItem
import dev.wign.pia.ui.theme.PiaAccent
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
    val sentiment: String,
    val symbolTag: String? = null
)

val SAMPLE_MARKET_NEWS = listOf(
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
fun NewsScreen(
    apiClient: PiaApiClient = remember { PiaApiClient() },
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf("social") } // "social" or "news"
    var socialPosts by remember { mutableStateOf<List<SocialPostItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        val posts = apiClient.getSocialPosts(35)
        if (posts.isNotEmpty()) {
            socialPosts = posts
        }
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Sub-Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeSubTab == "social") PiaAccent else PiaBorder.copy(alpha = 0.4f))
                    .clickable { activeSubTab = "social" }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(PiaUp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Social Pulse (X)",
                        color = if (activeSubTab == "social") PiaText else PiaTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeSubTab == "news") PiaAccent else PiaBorder.copy(alpha = 0.4f))
                    .clickable { activeSubTab = "news" }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Market News",
                    color = if (activeSubTab == "news") PiaText else PiaTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (isLoading && socialPosts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PiaAccent, modifier = Modifier.size(32.dp))
            }
        } else if (activeSubTab == "social") {
            // Live Social Pulse Stream
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(socialPosts) { post ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PiaCard)
                            .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        // Author header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(PiaBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (post.authorDisplayName.take(1).ifEmpty { "X" }).uppercase(),
                                        color = PiaAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = post.authorDisplayName.ifEmpty { post.authorUsername },
                                        color = PiaText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "@${post.authorUsername}",
                                        color = PiaTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Platform Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PiaBorder.copy(alpha = 0.5f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "𝕏 LIVE",
                                    color = PiaTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tweet text
                        Text(
                            text = post.text,
                            color = PiaText,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Footer info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(post.createdAt),
                                color = PiaTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (post.retweetCount > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Repeat,
                                            contentDescription = "Reposts",
                                            tint = PiaTextMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "${post.retweetCount}", color = PiaTextMuted, fontSize = 10.sp)
                                    }
                                }
                                if (post.likeCount > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ChatBubbleOutline,
                                            contentDescription = "Likes",
                                            tint = PiaTextMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "${post.likeCount}", color = PiaTextMuted, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Market News Stream
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SAMPLE_MARKET_NEWS) { news ->
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
                            Text(
                                text = "${news.source} • ${news.timeAgo}",
                                color = PiaTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )

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
}

private fun formatTime(iso: String): String {
    return if (iso.length >= 16) {
        iso.substring(11, 16) + " UTC"
    } else {
        "Recent"
    }
}
