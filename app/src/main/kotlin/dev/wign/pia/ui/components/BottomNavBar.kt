package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wign.pia.ui.theme.TvAccent
import dev.wign.pia.ui.theme.TvDarkBorder
import dev.wign.pia.ui.theme.TvDarkCard
import dev.wign.pia.ui.theme.TvDarkTextMuted
import dev.wign.pia.ui.theme.TvLightBorder
import dev.wign.pia.ui.theme.TvLightCard
import dev.wign.pia.ui.theme.TvLightTextMuted

data class NavTab(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    activeTab: String,
    isDarkMode: Boolean = true,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        NavTab("watchlist", "Watchlist", Icons.Default.ViewList),
        NavTab("chart", "Chart", Icons.Default.ShowChart),
        NavTab("markets", "Markets", Icons.Default.Analytics),
        NavTab("social", "News", Icons.Default.Feed),
        NavTab("settings", "Settings", Icons.Default.Settings)
    )

    val cardColor = if (isDarkMode) TvDarkCard else TvLightCard
    val borderColor = if (isDarkMode) TvDarkBorder else TvLightBorder
    val mutedColor = if (isDarkMode) TvDarkTextMuted else TvLightTextMuted

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(borderColor)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .navigationBarsPadding()
                .height(48.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = tab.id == activeTab
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab.id) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) TvAccent else mutedColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = tab.label,
                        color = if (isSelected) TvAccent else mutedColor,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
