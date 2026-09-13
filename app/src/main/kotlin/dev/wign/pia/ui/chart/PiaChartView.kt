package dev.wign.pia.ui.chart

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.tradingview.lightweightcharts.api.chart.models.color.IntColor
import com.tradingview.lightweightcharts.api.chart.models.color.surface.SolidColor
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.LayoutOptions
import com.tradingview.lightweightcharts.api.options.models.LineSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.PriceScaleOptions
import com.tradingview.lightweightcharts.api.options.models.TimeScaleOptions
import com.tradingview.lightweightcharts.api.series.enums.LineWidth
import com.tradingview.lightweightcharts.api.series.models.CandlestickData
import com.tradingview.lightweightcharts.api.series.models.LineData
import com.tradingview.lightweightcharts.api.series.models.Time
import com.tradingview.lightweightcharts.view.ChartsView
import dev.wign.pia.data.Candle
import dev.wign.pia.ui.theme.PiaBg

@Composable
fun PiaChartView(
    historicalCandles: List<Candle>,
    latestCandle: Candle?,
    emaSeries: List<Pair<Long, Double>>,
    latestEma: Double?,
    showEma: Boolean,
    onCrosshairMoved: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var candleSeriesApi by remember { mutableStateOf<SeriesApi?>(null) }
    var emaSeriesApi by remember { mutableStateOf<SeriesApi?>(null) }

    val chartsView = remember {
        ChartsView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            api.applyOptions {
                layout = LayoutOptions(
                    background = SolidColor(IntColor(0xFF131722.toInt())),
                    textColor = IntColor(0xFFD1D4DC.toInt())
                )
                timeScale = TimeScaleOptions(
                    borderColor = IntColor(0xFF2A2E39.toInt()),
                    timeVisible = true,
                    secondsVisible = false
                )
                rightPriceScale = PriceScaleOptions(
                    borderColor = IntColor(0xFF2A2E39.toInt())
                )
            }

            // Candlestick series
            api.addCandlestickSeries(
                options = CandlestickSeriesOptions(
                    upColor = IntColor(0xFF26A69A.toInt()),
                    downColor = IntColor(0xFFEF5350.toInt()),
                    borderVisible = false,
                    wickUpColor = IntColor(0xFF26A69A.toInt()),
                    wickDownColor = IntColor(0xFFEF5350.toInt())
                ),
                onSeriesCreated = { series ->
                    candleSeriesApi = series
                }
            )

            // EMA 20 overlay line series
            api.addLineSeries(
                options = LineSeriesOptions(
                    color = IntColor(0xFF2962FF.toInt()),
                    lineWidth = LineWidth.TWO,
                    priceLineVisible = false
                ),
                onSeriesCreated = { series ->
                    emaSeriesApi = series
                }
            )

            // Crosshair touch listener
            api.subscribeCrosshairMove { params ->
                val time = params.time
                if (time is Time.Utc) {
                    onCrosshairMoved(time.timestamp)
                } else {
                    onCrosshairMoved(null)
                }
            }
        }
    }

    // Set initial / historical candlestick data
    LaunchedEffect(historicalCandles, candleSeriesApi) {
        val api = candleSeriesApi
        if (historicalCandles.isNotEmpty() && api != null) {
            val list = historicalCandles
                .distinctBy { it.time }
                .sortedBy { it.time }
                .map { c ->
                    CandlestickData(
                        time = Time.Utc(c.time),
                        open = c.open.toFloat(),
                        high = c.high.toFloat(),
                        low = c.low.toFloat(),
                        close = c.close.toFloat()
                    )
                }
            api.setData(list)
        }
    }

    // Set / update EMA line series
    LaunchedEffect(emaSeries, showEma, emaSeriesApi) {
        val api = emaSeriesApi
        if (api != null) {
            if (showEma && emaSeries.isNotEmpty()) {
                val lineDataList = emaSeries.map { (timeSec, valDouble) ->
                    LineData(
                        time = Time.Utc(timeSec),
                        value = valDouble.toFloat()
                    )
                }
                api.setData(lineDataList)
            } else {
                api.setData(emptyList())
            }
        }
    }

    // Live tick / candle update
    LaunchedEffect(latestCandle) {
        val api = candleSeriesApi
        if (latestCandle != null && api != null) {
            val candleData = CandlestickData(
                time = Time.Utc(latestCandle.time),
                open = latestCandle.open.toFloat(),
                high = latestCandle.high.toFloat(),
                low = latestCandle.low.toFloat(),
                close = latestCandle.close.toFloat()
            )
            api.update(candleData)

            if (showEma && latestEma != null && emaSeriesApi != null) {
                emaSeriesApi?.update(
                    LineData(
                        time = Time.Utc(latestCandle.time),
                        value = latestEma.toFloat()
                    )
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(PiaBg)) {
        AndroidView(
            factory = { chartsView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
