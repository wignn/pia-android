package dev.wign.pia.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wign.pia.data.Candle
import dev.wign.pia.data.NativeBridge
import dev.wign.pia.data.PiaWsClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChartViewModel(
    private val wsClient: PiaWsClient = PiaWsClient()
) : ViewModel() {

    private val _currentSymbol = MutableStateFlow("BINANCE:BTCUSDT")
    val currentSymbol: StateFlow<String> = _currentSymbol.asStateFlow()

    private val _timeframe = MutableStateFlow("1m")
    val timeframe: StateFlow<String> = _timeframe.asStateFlow()

    private val _latestCandle = MutableStateFlow<Candle?>(null)
    val latestCandle: StateFlow<Candle?> = _latestCandle.asStateFlow()

    private val _lastPrice = MutableStateFlow(0.0)
    val lastPrice: StateFlow<Double> = _lastPrice.asStateFlow()

    init {
        NativeBridge.initConflator(60)
        wsClient.connect()
        subscribeSymbol(_currentSymbol.value)

        viewModelScope.launch {
            wsClient.ticks.collect { tick ->
                if (tick.symbol == _currentSymbol.value) {
                    _lastPrice.value = tick.price
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

    fun setTimeframe(tf: String) {
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
    }

    fun selectSymbol(symbol: String) {
        _currentSymbol.value = symbol
        subscribeSymbol(symbol)
    }

    private fun subscribeSymbol(symbol: String) {
        wsClient.subscribe(listOf("ticks:$symbol"))
    }

    override fun onCleared() {
        super.onCleared()
        wsClient.disconnect()
    }
}
