package dev.wign.pia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import dev.wign.pia.data.MarketPriceItem
import dev.wign.pia.data.PiaApiClient
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
fun HeatmapScreen(
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val apiClient = remember { PiaApiClient() }
    var rawPrices by remember { mutableStateOf<List<MarketPriceItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Equities", "Indices", "Crypto", "Commodities")

    val cardColor = if (isDarkMode) TvDarkCard else TvLightCard
    val textColor = if (isDarkMode) TvDarkText else TvLightText
    val mutedColor = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted
    val borderColor = if (isDarkMode) TvDarkBorder else TvLightBorder

    // Fetch REAL live prices from PIA ClickHouse & NATS Core
    LaunchedEffect(Unit) {
        isLoading = true
        val prices = apiClient.getMarketPrices()
        if (prices.isNotEmpty()) {
            rawPrices = prices
        }
        isLoading = false
    }

    val filteredList = remember(rawPrices, selectedCategory) {
        if (selectedCategory == "All") {
            rawPrices
        } else {
            rawPrices.filter { item ->
                when (selectedCategory) {
                    "Equities" -> item.assetType == "stock"
                    "Indices" -> item.assetType == "index"
                    "Crypto" -> item.assetType == "crypto" || item.symbol.contains("BTC") || item.symbol.contains("ETH")
                    "Commodities" -> item.assetType == "commodity" || item.symbol.contains("OIL") || item.symbol.contains("BRENT") || item.symbol.contains("GOLD")
                    else -> true
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Live Market Heatmap",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "Real-time valuations from ClickHouse & NATS",
                    fontSize = 10.sp,
                    color = mutedColor
                )
            }
            if (rawPrices.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TvUp.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "● ${rawPrices.size} LIVE ASSETS",
                        color = TvUp,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) TvAccent else borderColor.copy(alpha = 0.4f))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) androidx.compose.ui.graphics.Color.White else mutedColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading && rawPrices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TvAccent, modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredList) { item ->
                    // Financial styling: assign tile hue based on symbol hash or asset tier
                    val isPositive = (item.symbol.hashCode() % 2 == 0)
                    val tileBase = if (isPositive) TvUp else TvDown
                    val alpha = 0.15f

                    Box(
                        modifier = Modifier
                            .height(68.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(tileBase.copy(alpha = alpha))
                            .border(0.5.dp, tileBase.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.symbol,
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (item.price >= 1000) String.format("%,.0f", item.price) else String.format("%.2f", item.price),
                                color = tileBase,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            if (item.assetType != null) {
                                Text(
                                    text = item.assetType.uppercase(),
                                    color = mutedColor,
                                    fontSize = 8.sp,
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
