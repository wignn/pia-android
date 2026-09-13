package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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

data class WatchlistItem(
    val symbol: String,
    val name: String,
    val category: String,
    val samplePrice: Double = 0.0,
    val sampleChange: Double = 0.0
)

val INSTITUTIONAL_WATCHLIST = listOf(
    // === IDX STOCKS & INDICES ===
    WatchlistItem("IDX:BBCA", "Bank Central Asia", "IDX", 10250.0, 1.25),
    WatchlistItem("IDX:BBRI", "Bank Rakyat Indonesia", "IDX", 5150.0, -0.48),
    WatchlistItem("IDX:BMRI", "Bank Mandiri", "IDX", 7050.0, 0.71),
    WatchlistItem("IDX:BBNI", "Bank Negara Indonesia", "IDX", 5400.0, 0.0),
    WatchlistItem("IDX:TLKM", "Telkom Indonesia", "IDX", 3040.0, -1.30),
    WatchlistItem("IDX:ASII", "Astra International", "IDX", 5100.0, 0.99),
    WatchlistItem("IDX:ADRO", "Adaro Energy Indonesia", "IDX", 3890.0, 2.10),
    WatchlistItem("IDX:PTBA", "Bukit Asam", "IDX", 2710.0, 0.37),
    WatchlistItem("IDX:ANTM", "Aneka Tambang (Antam)", "IDX", 1620.0, 1.89),
    WatchlistItem("IDX:MDKA", "Merdeka Copper Gold", "IDX", 2380.0, -0.83),
    WatchlistItem("IDX:INCO", "Vale Indonesia", "IDX", 3980.0, 0.51),
    WatchlistItem("IDX:AMMN", "Amman Mineral Internasional", "IDX", 9450.0, 1.61),
    WatchlistItem("IDX:ICBP", "Indofood CBP Sukses Makmur", "IDX", 11950.0, -0.42),
    WatchlistItem("IDX:INDF", "Indofood Sukses Makmur", "IDX", 7100.0, 0.0),
    WatchlistItem("IDX:UNVR", "Unilever Indonesia", "IDX", 2180.0, -2.24),
    WatchlistItem("IDX:GOTO", "GoTo Gojek Tokopedia", "IDX", 58.0, 1.75),
    WatchlistItem("IDX:COMPOSITE", "IHSG Composite Index", "IDX", 7760.5, 0.35),

    // === CRYPTO ===
    WatchlistItem("BINANCE:BTCUSDT", "Bitcoin / Tether", "Crypto", 76800.0, 2.45),
    WatchlistItem("BINANCE:ETHUSDT", "Ethereum / Tether", "Crypto", 2480.0, 1.82),
    WatchlistItem("BINANCE:SOLUSDT", "Solana / Tether", "Crypto", 148.5, 4.12),
    WatchlistItem("BINANCE:BNBUSDT", "BNB / Tether", "Crypto", 580.2, 0.88),
    WatchlistItem("BINANCE:XRPUSDT", "Ripple / Tether", "Crypto", 0.584, -0.75),
    WatchlistItem("BINANCE:DOGEUSDT", "Dogecoin / Tether", "Crypto", 0.128, 3.20),
    WatchlistItem("BINANCE:ADAUSDT", "Cardano / Tether", "Crypto", 0.385, 0.26),
    WatchlistItem("BINANCE:AVAXUSDT", "Avalanche / Tether", "Crypto", 28.4, 2.15),
    WatchlistItem("BINANCE:LINKUSDT", "Chainlink / Tether", "Crypto", 11.9, 1.45),
    WatchlistItem("BINANCE:SUIUSDT", "Sui Network / Tether", "Crypto", 1.85, 8.40),

    // === US STOCKS ===
    WatchlistItem("US:NVDA", "NVIDIA Corporation", "US Stocks", 125.4, 3.15),
    WatchlistItem("US:AAPL", "Apple Inc.", "US Stocks", 228.2, 0.65),
    WatchlistItem("US:MSFT", "Microsoft Corporation", "US Stocks", 425.8, -0.42),
    WatchlistItem("US:TSLA", "Tesla Inc.", "US Stocks", 235.6, 4.80),
    WatchlistItem("US:GOOGL", "Alphabet Inc.", "US Stocks", 162.3, 0.28),
    WatchlistItem("US:AMZN", "Amazon.com Inc.", "US Stocks", 188.9, 1.12),
    WatchlistItem("US:META", "Meta Platforms Inc.", "US Stocks", 512.4, 1.74),
    WatchlistItem("US:AMD", "Advanced Micro Devices", "US Stocks", 152.8, 2.60),

    // === COMMODITIES ===
    WatchlistItem("COMM:XAUUSD", "Gold Spot / US Dollar", "Commodities", 2584.2, 0.85),
    WatchlistItem("COMM:XAGUSD", "Silver Spot / US Dollar", "Commodities", 30.75, 1.42),
    WatchlistItem("COMM:WTI", "Crude Oil WTI", "Commodities", 69.80, -1.25),
    WatchlistItem("COMM:BRENT", "Brent Crude Oil", "Commodities", 73.20, -1.10),

    // === FOREX ===
    WatchlistItem("FX:EURUSD", "Euro / US Dollar", "Forex", 1.1085, 0.12),
    WatchlistItem("FX:USDJPY", "US Dollar / Japanese Yen", "Forex", 142.30, -0.45),
    WatchlistItem("FX:GBPUSD", "British Pound / US Dollar", "Forex", 1.3140, 0.28),
    WatchlistItem("FX:USDIDR", "US Dollar / Indonesian Rupiah", "Forex", 15420.0, -0.15)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistSheet(
    sheetState: SheetState,
    currentSymbol: String,
    isDarkMode: Boolean = true,
    onSymbolSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "IDX", "Crypto", "US Stocks", "Commodities", "Forex")

    val filteredList = remember(searchQuery, selectedCategory) {
        INSTITUTIONAL_WATCHLIST.filter { item ->
            val matchesCat = selectedCategory == "All" || item.category == selectedCategory
            val matchesQuery = searchQuery.isEmpty() ||
                    item.symbol.contains(searchQuery, ignoreCase = true) ||
                    item.name.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

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
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Symbol Search & Watchlist",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = mutedColor)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text("Search BBCA, BTC, NVDA, GOLD...", color = mutedColor, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = mutedColor, modifier = Modifier.size(16.dp))
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

            // Category Selector Chips
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

            // Institutional Asset Rows (44dp compact TradingView table rows)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
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
                            .height(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) TvAccent.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable {
                                onSymbolSelected(item.symbol)
                                onDismiss()
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
                                    fontSize = 13.sp,
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
                                fontSize = 12.sp,
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
}
