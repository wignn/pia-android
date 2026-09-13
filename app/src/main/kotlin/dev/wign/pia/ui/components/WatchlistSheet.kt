package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted

data class WatchlistItem(
    val symbol: String,
    val name: String,
    val category: String
)

val DEFAULT_WATCHLIST = listOf(
    // IDX
    WatchlistItem("IDX:BBCA", "Bank Central Asia", "IDX"),
    WatchlistItem("IDX:BBRI", "Bank Rakyat Indonesia", "IDX"),
    WatchlistItem("IDX:BMRI", "Bank Mandiri", "IDX"),
    WatchlistItem("IDX:BBNI", "Bank Negara Indonesia", "IDX"),
    WatchlistItem("IDX:TLKM", "Telkom Indonesia", "IDX"),
    WatchlistItem("IDX:ASII", "Astra International", "IDX"),
    WatchlistItem("IDX:COMPOSITE", "IHSG Composite Index", "IDX"),
    // Crypto
    WatchlistItem("BINANCE:BTCUSDT", "Bitcoin / Tether", "Crypto"),
    WatchlistItem("BINANCE:ETHUSDT", "Ethereum / Tether", "Crypto"),
    WatchlistItem("BINANCE:SOLUSDT", "Solana / Tether", "Crypto"),
    WatchlistItem("BINANCE:BNBUSDT", "BNB / Tether", "Crypto"),
    // Global & Commodities
    WatchlistItem("CAPITALCOM:GOLD", "Gold Spot (USD)", "Commodities"),
    WatchlistItem("CAPITALCOM:SILVER", "Silver Spot (USD)", "Commodities"),
    WatchlistItem("CAPITALCOM:OIL_CRUDE", "Crude Oil", "Commodities"),
    WatchlistItem("US:SPX", "S&P 500 Index", "Indices"),
    WatchlistItem("US:NDX", "Nasdaq 100 Index", "Indices")
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Market Watchlist",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaText
                )
                IconButton(onClick = onDismiss) {
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
                placeholder = { Text("Search symbol or name...", color = PiaTextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PiaTextMuted
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

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) PiaAccent else PiaBorder)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) PiaText else PiaTextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Symbol List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredList) { item ->
                    val isCurrent = item.symbol == currentSymbol
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCurrent) PiaBorder else PiaCard)
                            .clickable {
                                onSymbolSelected(item.symbol)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = item.symbol,
                                color = if (isCurrent) PiaAccent else PiaText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.name,
                                color = PiaTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PiaBorder)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.category,
                                color = PiaTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
