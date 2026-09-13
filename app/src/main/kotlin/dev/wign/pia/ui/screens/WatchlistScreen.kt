package dev.wign.pia.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import dev.wign.pia.ui.components.INSTITUTIONAL_WATCHLIST
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
fun WatchlistScreen(
    currentSymbol: String,
    isDarkMode: Boolean = true,
    onSymbolSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "IDX", "Crypto", "US Stocks", "Commodities", "Forex")

    val filteredList = remember(searchQuery, selectedCategory) {
        INSTITUTIONAL_WATCHLIST.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            val matchesSearch = searchQuery.isEmpty() ||
                    item.symbol.contains(searchQuery, ignoreCase = true) ||
                    item.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val cardColor = if (isDarkMode) TvDarkCard else TvLightCard
    val textColor = if (isDarkMode) TvDarkText else TvLightText
    val mutedColor = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted
    val borderColor = if (isDarkMode) TvDarkBorder else TvLightBorder

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Institutional Watchlist",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = { Text("Search 45+ assets (BBCA, BTC, GOLD)...", color = mutedColor, fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = mutedColor,
                    modifier = Modifier.size(16.dp)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TvAccent,
                unfocusedBorderColor = borderColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category pills
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

        Spacer(modifier = Modifier.height(8.dp))

        // Watchlist item rows (46dp compact TradingView table rows)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(filteredList) { item ->
                val isCurrent = item.symbol.equals(currentSymbol, ignoreCase = true)
                val isPositive = item.sampleChange >= 0.0
                val badgeColor = if (isPositive) TvUp else TvDown

                val parts = item.symbol.split(":")
                val ticker = if (parts.size > 1) parts[1] else item.symbol
                val exchange = if (parts.size > 1) parts[0] else ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCurrent) TvAccent.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable {
                            onSymbolSelected(item.symbol)
                        }
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ticker,
                                color = if (isCurrent) TvAccent else textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (exchange.isNotEmpty()) {
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = exchange,
                                    color = mutedColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Text(
                            text = item.name,
                            color = mutedColor,
                            fontSize = 10.sp
                        )
                    }

                    // Right: Price and Change Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (item.samplePrice >= 1000) String.format("%,.0f", item.samplePrice) else String.format("%.2f", item.samplePrice),
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            val sign = if (isPositive) "+" else ""
                            Text(
                                text = "$sign${String.format("%.2f", item.sampleChange)}%",
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
