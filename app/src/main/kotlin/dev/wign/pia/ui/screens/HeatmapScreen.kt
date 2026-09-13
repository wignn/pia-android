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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

data class HeatmapTile(
    val symbol: String,
    val name: String,
    val changePercent: Double,
    val marketCapTier: String
)

val SAMPLE_HEATMAP = listOf(
    HeatmapTile("BBCA", "Bank Central Asia", 1.25, "Mega"),
    HeatmapTile("BBRI", "Bank Rakyat Indo", -0.48, "Mega"),
    HeatmapTile("BMRI", "Bank Mandiri", 0.71, "Mega"),
    HeatmapTile("TLKM", "Telkom Indonesia", -1.30, "Large"),
    HeatmapTile("ASII", "Astra Intl", 0.99, "Large"),
    HeatmapTile("BBNI", "Bank Negara Indo", 0.0, "Large"),
    HeatmapTile("BTC", "Bitcoin", 2.45, "Mega"),
    HeatmapTile("ETH", "Ethereum", -0.85, "Mega"),
    HeatmapTile("SOL", "Solana", 4.12, "Large"),
    HeatmapTile("NVDA", "Nvidia Corp", 3.20, "Mega"),
    HeatmapTile("AAPL", "Apple Inc", 0.45, "Mega"),
    HeatmapTile("MSFT", "Microsoft", -0.22, "Mega")
)

@Composable
fun HeatmapScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Cross-Asset Market Heatmap",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PiaText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(SAMPLE_HEATMAP) { tile ->
                val isUp = tile.changePercent >= 0.0
                val color = if (isUp) PiaUp else PiaDown
                val bgColor = color.copy(alpha = 0.18f)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgColor)
                        .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = tile.symbol,
                        color = PiaText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val sign = if (isUp) "+" else ""
                    Text(
                        text = "$sign${String.format("%.2f", tile.changePercent)}%",
                        color = color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
