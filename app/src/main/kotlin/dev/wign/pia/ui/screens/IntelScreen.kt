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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp

@Composable
fun IntelScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PIA Intelligence & AI Signals",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PiaText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Regime Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PiaCard)
                .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "CURRENT MARKET REGIME", color = PiaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PiaUp.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "EXPANSION", color = PiaUp, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bullish Trend — Volatility Compressed",
                color = PiaText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Equities and Crypto show sustained bid absorption. Momentum signals suggest minimal downside liquidation risk over the 4-hour window.",
                color = PiaTextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fear & Greed Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PiaCard)
                    .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(text = "FEAR & GREED", color = PiaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "78 / 100",
                    color = PiaUp,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(text = "Extreme Greed", color = PiaText, fontSize = 11.sp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PiaCard)
                    .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(text = "VOLATILITY INDEX", color = PiaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "14.82",
                    color = PiaAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(text = "Low Volatility", color = PiaText, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quantitative Breakdown
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PiaCard)
                .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Text(text = "CROSS-ASSET SENTIMENT HEAT", color = PiaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val assets = listOf(
                Pair("IDX Banking (BBCA/BMRI)", "Bullish Bias (+1.4σ)"),
                Pair("Crypto (BTC/ETH)", "Strong Inflow (+2.1σ)"),
                Pair("Gold Spot", "Neutral Consolidation (0.0σ)"),
                Pair("US Tech (NDX)", "Expansion (+1.8σ)")
            )

            assets.forEach { (asset, score) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = asset, color = PiaText, fontSize = 12.sp)
                    Text(text = score, color = PiaUp, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
