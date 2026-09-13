package dev.wign.pia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var hapticFeedback by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Terminal Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PiaText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Server Configuration Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PiaCard)
                .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Text(text = "NETWORK & GATEWAY", color = PiaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Gateway Endpoint", color = PiaText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "wss://api-engine.wign.dev/api/v1/ws",
                    color = PiaTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "REST History Endpoint", color = PiaText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "https://api-engine.wign.dev/api/v1/market/history",
                    color = PiaTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preferences
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PiaCard)
                .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Text(text = "FEED & PREFERENCES", color = PiaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tick Haptic Feedback", color = PiaText, fontSize = 13.sp)
                Switch(
                    checked = hapticFeedback,
                    onCheckedChange = { hapticFeedback = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = PiaAccent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Sound Alerts on Cross", color = PiaText, fontSize = 13.sp)
                Switch(
                    checked = soundEffects,
                    onCheckedChange = { soundEffects = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = PiaAccent)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // App Version
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PiaCard)
                .border(0.5.dp, PiaBorder, RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Text(text = "BUILD INFO", color = PiaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "PIA Terminal Android v1.0.0", color = PiaText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Powered by TradingView Lightweight Charts & Rust NDK", color = PiaTextMuted, fontSize = 11.sp)
        }
    }
}
