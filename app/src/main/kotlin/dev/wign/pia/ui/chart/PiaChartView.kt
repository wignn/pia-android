package dev.wign.pia.ui.chart

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.ChartOptions
import com.tradingview.lightweightcharts.api.options.models.Color
import com.tradingview.lightweightcharts.api.options.models.LayoutOptions
import com.tradingview.lightweightcharts.api.options.models.PriceScaleOptions
import com.tradingview.lightweightcharts.api.options.models.TimeScaleOptions
import com.tradingview.lightweightcharts.api.series.models.BarPrice
import com.tradingview.lightweightcharts.api.series.models.CandlestickData
import com.tradingview.lightweightcharts.api.series.models.Time
import com.tradingview.lightweightcharts.view.ChartsView
import dev.wign.pia.data.Candle
import dev.wign.pia.ui.theme.PiaBg

@Composable
fun PiaChartView(
    latestCandle: Candle?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var seriesApi: SeriesApi? = remember { null }

    val chartsView = remember {
        ChartsView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            api.applyOptions {
                layout = LayoutOptions(
                    background = Color(0xFF131722.toInt()),
                    textColor = Color(0xFFD1D4DC.toInt())
                )
                timeScale = TimeScaleOptions(
                    borderColor = Color(0xFF2A2E39.toInt()),
                    timeVisible = true,
                    secondsVisible = false
                )
                rightPriceScale = PriceScaleOptions(
                    borderColor = Color(0xFF2A2E39.toInt())
                )
            }
            api.addCandlestickSeries(
                options = CandlestickSeriesOptions(
                    upColor = Color(0xFF26A69A.toInt()),
                    downColor = Color(0xFFEF5350.toInt()),
                    borderVisible = false,
                    wickUpColor = Color(0xFF26A69A.toInt()),
                    wickDownColor = Color(0xFFEF5350.toInt())
                ),
                onSeriesCreated = { series ->
                    seriesApi = series
                }
            )
        }
    }

    LaunchedEffect(latestCandle) {
        if (latestCandle != null && seriesApi != null) {
            val candleData = CandlestickData(
                time = Time.Utc(latestCandle.time),
                open = BarPrice(latestCandle.open.toFloat()),
                high = BarPrice(latestCandle.high.toFloat()),
                low = BarPrice(latestCandle.low.toFloat()),
                close = BarPrice(latestCandle.close.toFloat())
            )
            seriesApi?.update(candleData)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(PiaBg)) {
        AndroidView(
            factory = { chartsView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
