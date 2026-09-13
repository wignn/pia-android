package dev.wign.pia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.wign.pia.ui.chart.ChartViewModel
import dev.wign.pia.ui.chart.PiaChartView
import dev.wign.pia.ui.components.BottomNavBar
import dev.wign.pia.ui.components.TopSymbolBar
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaTerminalTheme

class MainActivity : ComponentActivity() {
    private val chartViewModel: ChartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PiaTerminalTheme {
                MainScreen(chartViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: ChartViewModel) {
    val symbol by viewModel.currentSymbol.collectAsState()
    val timeframe by viewModel.timeframe.collectAsState()
    val lastPrice by viewModel.lastPrice.collectAsState()
    val latestCandle by viewModel.latestCandle.collectAsState()

    var activeTab by remember { mutableStateOf("chart") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PiaBg)
        ) {
            TopSymbolBar(
                symbol = symbol,
                lastPrice = lastPrice,
                currentTimeframe = timeframe,
                onTimeframeSelected = { viewModel.setTimeframe(it) }
            )

            PiaChartView(
                latestCandle = latestCandle,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
