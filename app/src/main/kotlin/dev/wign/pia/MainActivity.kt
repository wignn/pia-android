package dev.wign.pia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.wign.pia.ui.chart.ChartViewModel
import dev.wign.pia.ui.chart.PiaChartView
import dev.wign.pia.ui.components.BottomNavBar
import dev.wign.pia.ui.components.CrosshairHud
import dev.wign.pia.ui.components.RecentTradesTape
import dev.wign.pia.ui.components.TopSymbolBar
import dev.wign.pia.ui.components.WatchlistSheet
import dev.wign.pia.ui.screens.MarketsScreen
import dev.wign.pia.ui.screens.NewsScreen
import dev.wign.pia.ui.screens.SettingsScreen
import dev.wign.pia.ui.screens.WatchlistScreen
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaTerminalTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PiaTerminalTheme {
                val chartViewModel: ChartViewModel = viewModel()
                MainScreen(chartViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ChartViewModel) {
    val symbol by viewModel.currentSymbol.collectAsState()
    val timeframe by viewModel.timeframe.collectAsState()
    val lastPrice by viewModel.lastPrice.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val historicalCandles by viewModel.historicalCandles.collectAsState()
    val latestCandle by viewModel.latestCandle.collectAsState()

    val showEma by viewModel.showEma20.collectAsState()
    val emaValue by viewModel.ema20.collectAsState()
    val emaSeries by viewModel.emaSeries.collectAsState()
    val showRsi by viewModel.showRsi14.collectAsState()
    val rsiValue by viewModel.rsi14.collectAsState()
    val showTape by viewModel.showTape.collectAsState()
    val recentTrades by viewModel.recentTrades.collectAsState()
    val crosshairCandle by viewModel.crosshairCandle.collectAsState()

    var activeTab by remember { mutableStateOf("chart") }
    var isWatchlistModalOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val displayHudCandle = crosshairCandle ?: latestCandle ?: historicalCandles.lastOrNull()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                activeTab = activeTab,
                onTabSelected = { tab ->
                    activeTab = tab
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PiaBg)
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "chart" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopSymbolBar(
                            symbol = symbol,
                            lastPrice = lastPrice,
                            currentTimeframe = timeframe,
                            isConnected = isConnected,
                            onTimeframeSelected = { viewModel.setTimeframe(it) },
                            onOpenWatchlist = { isWatchlistModalOpen = true },
                            showEma = showEma,
                            emaValue = emaValue,
                            onToggleEma = { viewModel.toggleEma() },
                            showRsi = showRsi,
                            rsiValue = rsiValue,
                            onToggleRsi = { viewModel.toggleRsi() },
                            showTape = showTape,
                            onToggleTape = { viewModel.toggleTape() }
                        )

                        // Compact OHLCV crosshair HUD
                        CrosshairHud(candle = displayHudCandle)

                        // Full-bleed Lightweight Charts View
                        PiaChartView(
                            historicalCandles = historicalCandles,
                            latestCandle = latestCandle,
                            emaSeries = emaSeries,
                            latestEma = emaValue,
                            showEma = showEma,
                            onCrosshairMoved = { timeSec ->
                                viewModel.setCrosshairTimestamp(timeSec)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                        // Real-time trades tape drawer
                        if (showTape && recentTrades.isNotEmpty()) {
                            RecentTradesTape(trades = recentTrades)
                        }
                    }
                }
                "watchlist" -> {
                    WatchlistScreen(
                        currentSymbol = symbol,
                        onSymbolSelected = { selected ->
                            viewModel.selectSymbol(selected)
                            activeTab = "chart"
                        }
                    )
                }
                "markets" -> MarketsScreen()
                "social" -> NewsScreen()
                "settings" -> SettingsScreen()
            }

            if (isWatchlistModalOpen) {
                WatchlistSheet(
                    sheetState = sheetState,
                    currentSymbol = symbol,
                    onSymbolSelected = { selected ->
                        viewModel.selectSymbol(selected)
                    },
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            isWatchlistModalOpen = false
                        }
                    }
                )
            }
        }
    }
}
