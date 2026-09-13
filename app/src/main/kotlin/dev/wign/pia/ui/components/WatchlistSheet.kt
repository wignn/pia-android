package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
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
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaDown
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp

data class WatchlistItem(
    val symbol: String,
    val name: String,
    val category: String,
    val samplePrice: Double = 0.0,
    val sampleChange: Double = 0.0
)

val DEFAULT_WATCHLIST = listOf(
    // IDX
    WatchlistItem("IDX:BBCA", "Bank Central Asia", "IDX", 10250.0, 1.25),
    WatchlistItem("IDX:BBRI", "Bank Rakyat Indonesia", "IDX", 5150.0, -0.48),
    WatchlistItem("IDX:BMRI", "Bank Mandiri", "IDX", 7050.0, 0.71),
    WatchlistItem("IDX:BBNI", "Bank Negara Indonesia", "IDX", 5400.0, 0.0),
    WatchlistItem("IDX:TLKM", "Telkom Indonesia", "IDX", 3040.0, -1.30),
    WatchlistItem("IDX:ASII", "Astra International", "IDX", 5100.0, 0.99),
    WatchlistItem("IDX:COMPOSITE", "IHSG Composite", "IDX", 7760.5, 0.35),
    // Crypto
    WatchlistItem("BINANCE:BTCUSDT", "Bitcoin / Tether", "Crypto", 94250.0, 2.45),
    WatchlistItem("BINANCE:ETHUSDT", "Ethereum / Tether", "Crypto", 3350.0, -0.85),
    WatchlistItem("BINANCE:SOLUSDT", "Solana / Tether", "Crypto", 215.4, 4.12),
    WatchlistItem("BINANCE:BNBUSDT", "BNB / Tether", "Crypto", 680.2, 1.10),
    // Global & Commodities
    WatchlistItem("CAPITALCOM:GOLD", "Gold Spot (USD)", "Commodities", 2685.2, 0.42),
    WatchlistItem("CAPITALCOM:SILVER", "Silver Spot (USD)", "Commodities", 31.85, -0.65),
    WatchlistItem("CAPITALCOM:OIL_CRUDE", "Crude Oil (WTI)", "Commodities", 69.40, -1.15),
    WatchlistItem("US:SPX", "S&P 500 Index", "Indices", 5870.5, 0.55),
    WatchlistItem("US:NDX", "Nasdaq 100 Index", "Indices", 20450.0, 0.82)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistSheet(
    sheetState: SheetState,
    currentSymbol: String,
    onSymbolSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "IDX", "Crypto", "Commodities", "Indices")

    val filteredList = remember(searchQuery, selectedCategory) {
        DEFAULT_WATCHLIST.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            val matchesQuery = item.symbol.contains(searchQuery, ignoreCase = true) ||
                    item.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PiaCard,
        contentColor = PiaText
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Watchlist",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaText
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = PiaTextMuted
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search symbols, e.g. BBCA, BTC...", color = PiaTextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PiaTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PiaAccent,
                    unfocusedBorderColor = PiaBorder,
                    focusedTextColor = PiaText,
                    unfocusedTextColor = PiaText
                )
            )

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) PiaAccent else PiaBorder.copy(alpha = 0.5f))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) PiaText else PiaTextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // High Density Quote Rows
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(filteredList) { item ->
                    val isCurrent = item.symbol == currentSymbol
                    val isPositive = item.sampleChange >= 0.0
                    val badgeColor = if (isPositive) PiaUp else PiaDown

                    val parts = item.symbol.split(":")
                    val ticker = if (parts.size > 1) parts[1] else item.symbol
                    val exchange = if (parts.size > 1) parts[0] else ""

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) PiaBorder.copy(alpha = 0.6f) else PiaCard)
                            .clickable {
                                onSymbolSelected(item.symbol)
                                onDismiss()
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ticker,
                                    color = if (isCurrent) PiaAccent else PiaText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (exchange.isNotEmpty()) {
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(
                                        text = exchange,
                                        color = PiaTextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(
                                text = item.name,
                                color = PiaTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Right: Price and Change Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (item.samplePrice >= 1000) String.format("%,.0f", item.samplePrice) else String.format("%.2f", item.samplePrice),
                                color = PiaText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeColor.copy(alpha = 0.15f))
                                    .border(0.5.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
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
}
