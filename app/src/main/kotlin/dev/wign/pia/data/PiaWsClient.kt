package dev.wign.pia.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class PiaWsClient(
    private val baseUrl: String = "wss://api-engine.wign.dev/api/v1/ws",
    private val apiKey: String = "silvia",
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private var webSocket: WebSocket? = null
    private var isManualClose = false
    private var isAuthenticated = false

    private val _ticks = MutableSharedFlow<MarketTick>(extraBufferCapacity = 1024)
    val ticks: SharedFlow<MarketTick> = _ticks.asSharedFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val activeSymbols = mutableSetOf<String>()

    fun connect() {
        isManualClose = false
        val request = Request.Builder().url(baseUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Step 1: Send authentication frame immediately
                val authMsg = JSONObject().apply {
                    put("action", "auth")
                    put("api_key", apiKey)
                }.toString()
                webSocket.send(authMsg)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val root = JSONObject(text)

                    // Step 2: On successful authentication, send subscriptions
                    if (root.optString("event") == "authenticated") {
                        isAuthenticated = true
                        _isConnected.value = true
                        if (activeSymbols.isNotEmpty()) {
                            sendSubscription(activeSymbols.toList())
                        }
                        return
                    }

                    // Step 3: Handle streaming market_data ticks
                    if (root.optString("channel") == "market_data") {
                        val data = root.optJSONObject("data") ?: return
                        val tickObj = data.optJSONObject("tick") ?: return
                        val symbol = tickObj.optString("symbol")
                        val price = tickObj.optDouble("price", 0.0)
                        val volume = tickObj.optDouble("quantity", tickObj.optDouble("volume", 0.0))

                        var timestamp = tickObj.optLong("trade_time_ms", 0L)
                        if (timestamp == 0L) {
                            timestamp = tickObj.optLong("timestamp_ms", 0L)
                        }
                        if (timestamp == 0L) {
                            val receivedAt = tickObj.optString("received_at")
                            if (receivedAt.isNotEmpty()) {
                                timestamp = parseIsoToMs(receivedAt)
                            }
                        }
                        if (timestamp == 0L) {
                            timestamp = System.currentTimeMillis()
                        }

                        if (symbol.isNotEmpty() && price > 0.0) {
                            scope.launch {
                                _ticks.emit(MarketTick(symbol, price, volume, timestamp))
                            }
                        }
                        return
                    }

                    // Direct tick payload fallback
                    if (root.has("symbol") && root.has("price")) {
                        val symbol = root.getString("symbol")
                        val price = root.getDouble("price")
                        val volume = root.optDouble("volume", 0.0)
                        scope.launch {
                            _ticks.emit(MarketTick(symbol, price, volume, System.currentTimeMillis()))
                        }
                    }
                } catch (_: Exception) {}
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _isConnected.value = false
                isAuthenticated = false
                if (!isManualClose) {
                    scheduleReconnect()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _isConnected.value = false
                isAuthenticated = false
                if (!isManualClose) {
                    scheduleReconnect()
                }
            }
        })
    }

    fun subscribe(symbols: List<String>) {
        val cleaned = symbols.map { if (it.contains(":")) it.substringAfter(":") else it }
        activeSymbols.addAll(cleaned)
        if (isAuthenticated) {
            sendSubscription(cleaned)
        }
    }

    private fun sendSubscription(symbols: List<String>) {
        try {
            val arr = JSONArray()
            symbols.forEach { arr.put(it) }
            val subMsg = JSONObject().apply {
                put("action", "subscribe")
                put("symbols", arr)
            }.toString()
            webSocket?.send(subMsg)
        } catch (_: Exception) {}
    }

    private fun scheduleReconnect() {
        scope.launch {
            delay(3000)
            if (!isManualClose && !_isConnected.value) {
                connect()
            }
        }
    }

    fun disconnect() {
        isManualClose = true
        _isConnected.value = false
        isAuthenticated = false
        webSocket?.close(1000, "Normal closure")
        webSocket = null
    }

    private fun parseIsoToMs(iso: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            sdf.parse(iso.take(19))?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}
