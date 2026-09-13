package dev.wign.pia.ui.chart

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.Color
import com.tradingview.lightweightcharts.api.options.models.LayoutOptions
import com.tradingview.lightweightcharts.api.options.models.LineSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.LineWidth
import com.tradingview.lightweightcharts.api.options.models.PriceScaleOptions
import com.tradingview.lightweightcharts.api.options.models.TimeScaleOptions
import com.tradingview.lightweightcharts.api.series.models.BarPrice
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
    var candleSeriesApi: SeriesApi? = remember { null }
    var emaSeriesApi: SeriesApi? = remember { null }

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

            // Candlestick series
            api.addCandlestickSeries(
                options = CandlestickSeriesOptions(
                    upColor = Color(0xFF26A69A.toInt()),
                    downColor = Color(0xFFEF5350.toInt()),
                    borderVisible = false,
                    wickUpColor = Color(0xFF26A69A.toInt()),
                    wickDownColor = Color(0xFFEF5350.toInt())
                ),
                onSeriesCreated = { series ->
                    candleSeriesApi = series
                }
            )

            // EMA 20 overlay line series
            api.addLineSeries(
                options = LineSeriesOptions(
                    color = Color(0xFF2962FF.toInt()),
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
                    onCrosshairMoved(time.value)
                } else {
                    onCrosshairMoved(null)
                }
            }
        }
    }

    // Set initial / historical candlestick data
    LaunchedEffect(historicalCandles) {
        if (historicalCandles.isNotEmpty() && candleSeriesApi != null) {
            val list = historicalCandles.map { c ->
                CandlestickData(
                    time = Time.Utc(c.time),
                    open = BarPrice(c.open.toFloat()),
                    high = BarPrice(c.high.toFloat()),
                    low = BarPrice(c.low.toFloat()),
                    close = BarPrice(c.close.toFloat())
                )
            }
            candleSeriesApi?.setData(list)
        }
    }

    // Set / update EMA line series
    LaunchedEffect(emaSeries, showEma) {
        if (emaSeriesApi != null) {
            if (showEma && emaSeries.isNotEmpty()) {
                val lineDataList = emaSeries.map { (timeSec, valDouble) ->
                    LineData(
                        time = Time.Utc(timeSec),
                        value = BarPrice(valDouble.toFloat())
                    )
                }
                emaSeriesApi?.setData(lineDataList)
            } else {
                emaSeriesApi?.setData(emptyList())
            }
        }
    }

    // Live tick / candle update
    LaunchedEffect(latestCandle) {
        if (latestCandle != null && candleSeriesApi != null) {
            val candleData = CandlestickData(
                time = Time.Utc(latestCandle.time),
                open = BarPrice(latestCandle.open.toFloat()),
                high = BarPrice(latestCandle.high.toFloat()),
                low = BarPrice(latestCandle.low.toFloat()),
                close = BarPrice(latestCandle.close.toFloat())
            )
            candleSeriesApi?.update(candleData)

            if (showEma && latestEma != null && emaSeriesApi != null) {
                emaSeriesApi?.update(
                    LineData(
                        time = Time.Utc(latestCandle.time),
                        value = BarPrice(latestEma.toFloat())
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
