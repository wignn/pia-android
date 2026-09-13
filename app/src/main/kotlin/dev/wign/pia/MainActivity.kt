package dev.wign.pia

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.wign.pia.ui.chart.ChartViewModel
import dev.wign.pia.ui.chart.PiaChartView
import dev.wign.pia.ui.components.BottomNavBar
import dev.wign.pia.ui.components.CrosshairHud
import dev.wign.pia.ui.components.PriceAlertDialog
import dev.wign.pia.ui.components.RecentTradesTape
import dev.wign.pia.ui.components.TopSymbolBar
import dev.wign.pia.ui.components.WatchlistSheet
import dev.wign.pia.ui.screens.MarketsScreen
import dev.wign.pia.ui.screens.NewsScreen
import dev.wign.pia.ui.screens.SettingsScreen
import dev.wign.pia.ui.screens.WatchlistScreen
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBg
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaTerminalTheme
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val symbol by viewModel.currentSymbol.collectAsState()
    val timeframe by viewModel.timeframe.collectAsState()
    val chartType by viewModel.chartType.collectAsState()
    val lastPrice by viewModel.lastPrice.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val historicalCandles by viewModel.historicalCandles.collectAsState()
    val latestCandle by viewModel.latestCandle.collectAsState()

    val showEma by viewModel.showEma20.collectAsState()
    val emaValue by viewModel.ema20.collectAsState()
    val emaSeries by viewModel.emaSeries.collectAsState()
    val showRsi by viewModel.showRsi14.collectAsState()
    val rsiValue by viewModel.rsi14.collectAsState()
    val showVolume by viewModel.showVolume.collectAsState()
    val showTape by viewModel.showTape.collectAsState()
    val recentTrades by viewModel.recentTrades.collectAsState()
    val crosshairCandle by viewModel.crosshairCandle.collectAsState()

    var activeTab by remember { mutableStateOf("chart") }
    var isWatchlistModalOpen by remember { mutableStateOf(false) }
    var isAlertModalOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val displayHudCandle = crosshairCandle ?: latestCandle ?: historicalCandles.lastOrNull()

    // Handle Price Alert Triggers with Haptic Vibration & Notification
    LaunchedEffect(Unit) {
        viewModel.triggeredAlert.collect { alert ->
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(VibrationEffect.createOneShot(350L, VibrationEffect.DEFAULT_AMPLITUDE))
            Toast.makeText(
                context,
                "🔔 ALERT TRIGGERED: ${alert.symbol} crossed ${String.format("%,.2f", alert.targetPrice)}!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!isLandscape) {
                BottomNavBar(
                    activeTab = activeTab,
                    onTabSelected = { tab ->
                        activeTab = tab
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PiaBg)
                .padding(if (isLandscape) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
        ) {
            when {
                isLandscape -> {
                    // Landscape Auto Fullscreen Mode
                    Box(modifier = Modifier.fillMaxSize()) {
                        PiaChartView(
                            historicalCandles = historicalCandles,
                            latestCandle = latestCandle,
                            emaSeries = emaSeries,
                            latestEma = emaValue,
                            showEma = showEma,
                            showVolume = showVolume,
                            chartType = chartType,
                            onCrosshairMoved = { timeSec ->
                                viewModel.setCrosshairTimestamp(timeSec)
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Translucent Floating Landscape Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PiaCard.copy(alpha = 0.85f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = symbol,
                                    color = PiaText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lastPrice > 0) String.format("%,.2f", lastPrice) else "--",
                                    color = PiaUp,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Compact Timeframe & Indicator Selector
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("1m", "5m", "15m", "1h", "1D").forEach { tf ->
                                    val isSelected = tf == timeframe
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) PiaAccent else PiaBorder.copy(alpha = 0.5f))
                                            .clickable { viewModel.setTimeframe(tf) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = tf,
                                            color = if (isSelected) PiaText else PiaTextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (showVolume) PiaAccent.copy(alpha = 0.3f) else PiaBorder.copy(alpha = 0.4f))
                                        .clickable { viewModel.toggleVolume() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "VOL",
                                        color = if (showVolume) PiaAccent else PiaTextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                activeTab == "chart" -> {
                    // Portrait Chart View
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopSymbolBar(
                            symbol = symbol,
                            lastPrice = lastPrice,
                            currentTimeframe = timeframe,
                            chartType = chartType,
                            isConnected = isConnected,
                            onTimeframeSelected = { viewModel.setTimeframe(it) },
                            onCycleChartType = { viewModel.cycleChartType() },
                            onOpenWatchlist = { isWatchlistModalOpen = true },
                            showEma = showEma,
                            emaValue = emaValue,
                            onToggleEma = { viewModel.toggleEma() },
                            showRsi = showRsi,
                            rsiValue = rsiValue,
                            onToggleRsi = { viewModel.toggleRsi() },
                            showVolume = showVolume,
                            onToggleVolume = { viewModel.toggleVolume() },
                            showTape = showTape,
                            onToggleTape = { viewModel.toggleTape() },
                            onOpenAlertModal = { isAlertModalOpen = true }
                        )

                        // Compact OHLCV crosshair HUD
                        CrosshairHud(candle = displayHudCandle)

                        // Full-bleed Lightweight Charts View with Volume & S/R Lines
                        PiaChartView(
                            historicalCandles = historicalCandles,
                            latestCandle = latestCandle,
                            emaSeries = emaSeries,
                            latestEma = emaValue,
                            showEma = showEma,
                            showVolume = showVolume,
                            chartType = chartType,
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

                activeTab == "watchlist" -> {
                    WatchlistScreen(
                        currentSymbol = symbol,
                        onSymbolSelected = { selected ->
                            viewModel.selectSymbol(selected)
                            activeTab = "chart"
                        }
                    )
                }

                activeTab == "markets" -> MarketsScreen()
                activeTab == "social" -> NewsScreen()
                activeTab == "settings" -> SettingsScreen()
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

            if (isAlertModalOpen) {
                PriceAlertDialog(
                    symbol = symbol,
                    currentPrice = lastPrice,
                    onSetAlert = { target, condition ->
                        viewModel.addPriceAlert(target, condition)
                        Toast.makeText(context, "Alert set for $symbol at $target ($condition)", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { isAlertModalOpen = false }
                )
            }
        }
    }
}
