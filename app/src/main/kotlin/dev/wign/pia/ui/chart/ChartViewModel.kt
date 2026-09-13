package dev.wign.pia.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wign.pia.data.Candle
import dev.wign.pia.data.NativeBridge
import dev.wign.pia.data.PiaApiClient
import dev.wign.pia.data.PiaWsClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChartViewModel(
    private val wsClient: PiaWsClient = PiaWsClient(),
    private val apiClient: PiaApiClient = PiaApiClient()
) : ViewModel() {

    private val _currentSymbol = MutableStateFlow("BINANCE:BTCUSDT")
    val currentSymbol: StateFlow<String> = _currentSymbol.asStateFlow()

    private val _timeframe = MutableStateFlow("1m")
    val timeframe: StateFlow<String> = _timeframe.asStateFlow()

    private val _historicalCandles = MutableStateFlow<List<Candle>>(emptyList())
    val historicalCandles: StateFlow<List<Candle>> = _historicalCandles.asStateFlow()

    private val _latestCandle = MutableStateFlow<Candle?>(null)
    val latestCandle: StateFlow<Candle?> = _latestCandle.asStateFlow()

    private val _lastPrice = MutableStateFlow(0.0)
    val lastPrice: StateFlow<Double> = _lastPrice.asStateFlow()

    private val _ema20 = MutableStateFlow<Double?>(null)
    val ema20: StateFlow<Double?> = _ema20.asStateFlow()

    private val _emaSeries = MutableStateFlow<List<Pair<Long, Double>>>(emptyList())
    val emaSeries: StateFlow<List<Pair<Long, Double>>> = _emaSeries.asStateFlow()

    private val _rsi14 = MutableStateFlow<Double?>(null)
    val rsi14: StateFlow<Double?> = _rsi14.asStateFlow()

    private val _showEma20 = MutableStateFlow(true)
    val showEma20: StateFlow<Boolean> = _showEma20.asStateFlow()

    private val _showRsi14 = MutableStateFlow(false)
    val showRsi14: StateFlow<Boolean> = _showRsi14.asStateFlow()

    private val _crosshairCandle = MutableStateFlow<Candle?>(null)
    val crosshairCandle: StateFlow<Candle?> = _crosshairCandle.asStateFlow()

    init {
        NativeBridge.initConflator(60)
        wsClient.connect()
        loadSymbolData(_currentSymbol.value, _timeframe.value)

        viewModelScope.launch {
            wsClient.ticks.collect { tick ->
                val cleanCurrent = if (_currentSymbol.value.contains(":")) {
                    _currentSymbol.value.substringAfter(":")
                } else {
                    _currentSymbol.value
                }

                if (tick.symbol.equals(cleanCurrent, ignoreCase = true) ||
                    tick.symbol.equals(_currentSymbol.value, ignoreCase = true)
                ) {
                    _lastPrice.value = tick.price

                    // Native NDK calculation for real-time EMA & RSI
                    val ema = NativeBridge.calculateEma(20, tick.price)
                    _ema20.value = ema

                    val rsi = NativeBridge.calculateRsi(14, tick.price)
                    if (rsi >= 0.0) {
                        _rsi14.value = rsi
                    }

                    val candle = NativeBridge.conflateTick(
                        symbol = tick.symbol,
                        price = tick.price,
                        volume = tick.volume,
                        timestampMs = tick.timestamp
                    )
                    if (candle != null) {
                        _latestCandle.value = candle
                    }
                }
            }
        }
    }

    fun selectSymbol(symbol: String) {
        if (_currentSymbol.value == symbol) return
        _currentSymbol.value = symbol
        loadSymbolData(symbol, _timeframe.value)
    }

    fun setTimeframe(tf: String) {
        if (_timeframe.value == tf) return
        _timeframe.value = tf
        val sec = when (tf) {
            "1m" -> 60L
            "5m" -> 300L
            "15m" -> 900L
            "1h" -> 3600L
            "1D" -> 86400L
            else -> 60L
        }
        NativeBridge.initConflator(sec)
        loadSymbolData(_currentSymbol.value, tf)
    }

    fun toggleEma() {
        _showEma20.value = !_showEma20.value
    }

    fun toggleRsi() {
        _showRsi14.value = !_showRsi14.value
    }

    fun setCrosshairTimestamp(timeSec: Long?) {
        if (timeSec == null) {
            _crosshairCandle.value = null
            return
        }
        val list = _historicalCandles.value
        val match = list.find { it.time == timeSec } ?: list.lastOrNull()
        _crosshairCandle.value = match
    }

    private fun loadSymbolData(symbol: String, timeframe: String) {
        val cleanSymbol = if (symbol.contains(":")) symbol.substringAfter(":") else symbol
        wsClient.subscribe(listOf(cleanSymbol))

        viewModelScope.launch {
            val candles = apiClient.getHistory(symbol, timeframe, limit = 150)
            if (candles.isNotEmpty()) {
                _historicalCandles.value = candles
                _lastPrice.value = candles.last().close

                // Compute initial batch EMA curve via NDK
                val closes = candles.map { it.close }.toDoubleArray()
                val emaValues = NativeBridge.computeEmaSeries(20, closes)
                _emaSeries.value = candles.mapIndexed { i, c -> Pair(c.time, emaValues[i]) }
                _ema20.value = emaValues.lastOrNull()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsClient.disconnect()
    }
}
