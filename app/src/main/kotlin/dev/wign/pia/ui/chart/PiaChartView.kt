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
import com.tradingview.lightweightcharts.api.options.models.AreaSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.BarSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.CrosshairLineOptions
import com.tradingview.lightweightcharts.api.options.models.CrosshairOptions
import com.tradingview.lightweightcharts.api.options.models.GridLineOptions
import com.tradingview.lightweightcharts.api.options.models.GridOptions
import com.tradingview.lightweightcharts.api.options.models.HistogramSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.LayoutOptions
import com.tradingview.lightweightcharts.api.options.models.LineSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.PriceLineOptions
import com.tradingview.lightweightcharts.api.options.models.PriceScaleMargins
import com.tradingview.lightweightcharts.api.options.models.PriceScaleOptions
import com.tradingview.lightweightcharts.api.options.models.TimeScaleOptions
import com.tradingview.lightweightcharts.api.series.common.PriceLine
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode
import com.tradingview.lightweightcharts.api.series.enums.LineStyle
import com.tradingview.lightweightcharts.api.series.enums.LineWidth
import com.tradingview.lightweightcharts.api.series.models.AreaData
import com.tradingview.lightweightcharts.api.series.models.BarData
import com.tradingview.lightweightcharts.api.series.models.CandlestickData
import com.tradingview.lightweightcharts.api.series.models.HistogramData
import com.tradingview.lightweightcharts.api.series.models.LineData
import com.tradingview.lightweightcharts.api.series.models.PriceScaleId
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
    showVolume: Boolean = true,
    showSrLines: Boolean = true,
    chartType: String = "candles",
    onCrosshairMoved: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mainSeriesApi by remember { mutableStateOf<SeriesApi?>(null) }
    var volumeSeriesApi by remember { mutableStateOf<SeriesApi?>(null) }
    var emaSeriesApi by remember { mutableStateOf<SeriesApi?>(null) }

    var highPriceLine by remember { mutableStateOf<PriceLine?>(null) }
    var lowPriceLine by remember { mutableStateOf<PriceLine?>(null) }

    val chartsView = remember(chartType) {
        ChartsView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            api.applyOptions {
                layout = LayoutOptions(
                    background = SolidColor(IntColor(0xFF0E1118.toInt())),
                    textColor = IntColor(0xFF9EA2AE.toInt())
                )
                grid = GridOptions(
                    vertLines = GridLineOptions(visible = false),
                    horzLines = GridLineOptions(visible = false)
                )
                crosshair = CrosshairOptions(
                    mode = CrosshairMode.NORMAL,
                    vertLine = CrosshairLineOptions(
                        color = IntColor(0x33787B86.toInt()),
                        style = LineStyle.DASHED
                    ),
                    horzLine = CrosshairLineOptions(
                        color = IntColor(0x33787B86.toInt()),
                        style = LineStyle.DASHED
                    )
                )
                timeScale = TimeScaleOptions(
                    borderColor = IntColor(0xFF1E222D.toInt()),
                    timeVisible = true,
                    secondsVisible = false
                )
                rightPriceScale = PriceScaleOptions(
                    borderColor = IntColor(0xFF1E222D.toInt())
                )
            }

            // Volume Histogram Series with isolated priceScale margins
            api.addHistogramSeries(
                options = HistogramSeriesOptions(
                    priceLineVisible = false
                ),
                onSeriesCreated = { series ->
                    volumeSeriesApi = series
                    series.priceScale().applyOptions(
                        PriceScaleOptions(
                            scaleMargins = PriceScaleMargins(top = 0.82f, bottom = 0.0f)
                        )
                    )
                }
            )

            // Dynamic Main Series according to chartType
            when (chartType) {
                "line" -> {
                    api.addLineSeries(
                        options = LineSeriesOptions(
                            color = IntColor(0xFF26A69A.toInt()),
                            lineWidth = LineWidth.TWO
                        ),
                        onSeriesCreated = { series -> mainSeriesApi = series }
                    )
                }
                "area" -> {
                    api.addAreaSeries(
                        options = AreaSeriesOptions(
                            topColor = IntColor(0x3326A69A.toInt()),
                            bottomColor = IntColor(0x0026A69A.toInt()),
                            lineColor = IntColor(0xFF26A69A.toInt()),
                            lineWidth = LineWidth.TWO
                        ),
                        onSeriesCreated = { series -> mainSeriesApi = series }
                    )
                }
                "bars" -> {
                    api.addBarSeries(
                        options = BarSeriesOptions(
                            upColor = IntColor(0xFF26A69A.toInt()),
                            downColor = IntColor(0xFFEF5350.toInt())
                        ),
                        onSeriesCreated = { series -> mainSeriesApi = series }
                    )
                }
                else -> {
                    api.addCandlestickSeries(
                        options = CandlestickSeriesOptions(
                            upColor = IntColor(0xFF26A69A.toInt()),
                            downColor = IntColor(0xFFEF5350.toInt()),
                            borderVisible = false,
                            wickUpColor = IntColor(0xFF26A69A.toInt()),
                            wickDownColor = IntColor(0xFFEF5350.toInt())
                        ),
                        onSeriesCreated = { series -> mainSeriesApi = series }
                    )
                }
            }

            // Overlay EMA 20 line series
            api.addLineSeries(
                options = LineSeriesOptions(
                    color = IntColor(0xFF2962FF.toInt()),
                    lineWidth = LineWidth.TWO,
                    priceLineVisible = false
                ),
                onSeriesCreated = { series -> emaSeriesApi = series }
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

    // Set initial / historical main dataset, volume, and Support/Resistance lines
    LaunchedEffect(historicalCandles, mainSeriesApi, volumeSeriesApi, chartType, showVolume, showSrLines) {
        val api = mainSeriesApi
        val volApi = volumeSeriesApi
        if (historicalCandles.isNotEmpty()) {
            val distinctList = historicalCandles
                .distinctBy { it.time }
                .sortedBy { it.time }

            // Update main series
            if (api != null) {
                when (chartType) {
                    "line" -> {
                        val lineData = distinctList.map { c ->
                            LineData(time = Time.Utc(c.time), value = c.close.toFloat())
                        }
                        api.setData(lineData)
                    }
                    "area" -> {
                        val areaData = distinctList.map { c ->
                            AreaData(time = Time.Utc(c.time), value = c.close.toFloat())
                        }
                        api.setData(areaData)
                    }
                    "bars" -> {
                        val barData = distinctList.map { c ->
                            BarData(
                                time = Time.Utc(c.time),
                                open = c.open.toFloat(),
                                high = c.high.toFloat(),
                                low = c.low.toFloat(),
                                close = c.close.toFloat()
                            )
                        }
                        api.setData(barData)
                    }
                    else -> {
                        val candleData = distinctList.map { c ->
                            CandlestickData(
                                time = Time.Utc(c.time),
                                open = c.open.toFloat(),
                                high = c.high.toFloat(),
                                low = c.low.toFloat(),
                                close = c.close.toFloat()
                            )
                        }
                        api.setData(candleData)
                    }
                }

                // 24h High & Low Support/Resistance price lines
                if (showSrLines) {
                    val maxHigh = distinctList.maxOfOrNull { it.high }?.toFloat()
                    val minLow = distinctList.minOfOrNull { it.low }?.toFloat()

                    // Clear old price lines if present
                    highPriceLine?.let { api.removePriceLine(it) }
                    lowPriceLine?.let { api.removePriceLine(it) }

                    if (maxHigh != null) {
                        highPriceLine = api.createPriceLine(
                            PriceLineOptions(
                                price = maxHigh,
                                color = IntColor(0xFFEF5350.toInt()),
                                lineWidth = LineWidth.ONE,
                                lineStyle = LineStyle.DOTTED,
                                title = "HIGH"
                            )
                        )
                    }

                    if (minLow != null) {
                        lowPriceLine = api.createPriceLine(
                            PriceLineOptions(
                                price = minLow,
                                color = IntColor(0xFF26A69A.toInt()),
                                lineWidth = LineWidth.ONE,
                                lineStyle = LineStyle.DOTTED,
                                title = "LOW"
                            )
                        )
                    }
                }
            }

            // Update volume histogram series
            if (volApi != null) {
                if (showVolume) {
                    val volData = distinctList.map { c ->
                        val isUp = c.close >= c.open
                        val colorInt = if (isUp) 0x6626A69A.toInt() else 0x66EF5350.toInt()
                        HistogramData(
                            time = Time.Utc(c.time),
                            value = c.volume.toFloat(),
                            color = IntColor(colorInt)
                        )
                    }
                    volApi.setData(volData)
                } else {
                    volApi.setData(emptyList())
                }
            }
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

    // Live tick / candle & volume update
    LaunchedEffect(latestCandle) {
        val api = mainSeriesApi
        val volApi = volumeSeriesApi
        if (latestCandle != null) {
            if (api != null) {
                when (chartType) {
                    "line" -> {
                        api.update(LineData(time = Time.Utc(latestCandle.time), value = latestCandle.close.toFloat()))
                    }
                    "area" -> {
                        api.update(AreaData(time = Time.Utc(latestCandle.time), value = latestCandle.close.toFloat()))
                    }
                    "bars" -> {
                        api.update(
                            BarData(
                                time = Time.Utc(latestCandle.time),
                                open = latestCandle.open.toFloat(),
                                high = latestCandle.high.toFloat(),
                                low = latestCandle.low.toFloat(),
                                close = latestCandle.close.toFloat()
                            )
                        )
                    }
                    else -> {
                        api.update(
                            CandlestickData(
                                time = Time.Utc(latestCandle.time),
                                open = latestCandle.open.toFloat(),
                                high = latestCandle.high.toFloat(),
                                low = latestCandle.low.toFloat(),
                                close = latestCandle.close.toFloat()
                            )
                        )
                    }
                }
            }

            if (showVolume && volApi != null) {
                val isUp = latestCandle.close >= latestCandle.open
                val colorInt = if (isUp) 0x6626A69A.toInt() else 0x66EF5350.toInt()
                volApi.update(
                    HistogramData(
                        time = Time.Utc(latestCandle.time),
                        value = latestCandle.volume.toFloat(),
                        color = IntColor(colorInt)
                    )
                )
            }

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
