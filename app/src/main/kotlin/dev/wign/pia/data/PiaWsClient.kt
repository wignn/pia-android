package dev.wign.pia.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PiaWsClient(
    private val baseUrl: String = "wss://api-engine.wign.dev/api/v1/ws",
    private val apiKey: String = "silvia",
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private var webSocket: WebSocket? = null
    private val _ticks = MutableSharedFlow<MarketTick>(extraBufferCapacity = 512)
    val ticks: SharedFlow<MarketTick> = _ticks.asSharedFlow()

    private val activeSymbols = mutableSetOf<String>()

    fun connect(ticket: String? = null) {
        val url = if (!ticket.isNullOrBlank()) "$baseUrl?ticket=$ticket" else baseUrl
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Send authentication frame immediately to prevent 5-second auth_timeout
                val authMsg = JSONObject().apply {
                    put("action", "auth")
                    put("api_key", apiKey)
                }.toString()
                webSocket.send(authMsg)

                // Resubscribe symbols on open
                if (activeSymbols.isNotEmpty()) {
                    sendSubscription(activeSymbols.toList())
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val root = JSONObject(text)

                    // Case 1: Gateway market_data channel envelope
                    if (root.optString("channel") == "market_data") {
                        val data = root.optJSONObject("data") ?: return
                        val tickObj = data.optJSONObject("tick") ?: return
                        val symbol = tickObj.optString("symbol")
                        val price = tickObj.optDouble("price", 0.0)
                        val volume = tickObj.optDouble("quantity", tickObj.optDouble("volume", 0.0))
                        if (symbol.isNotEmpty() && price > 0.0) {
                            scope.launch {
                                _ticks.emit(MarketTick(symbol, price, volume, System.currentTimeMillis()))
                            }
                        }
                        return
                    }

                    // Case 2: Direct tick payload
                    if (root.has("symbol") && root.has("price")) {
                        val symbol = root.getString("symbol")
                        val price = root.getDouble("price")
                        val volume = root.optDouble("volume", 0.0)
                        scope.launch {
                            _ticks.emit(MarketTick(symbol, price, volume, System.currentTimeMillis()))
                        }
                    }
                } catch (_: Exception) {
                    // Ignore non-json or control frames
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Background reconnect on failure
            }
        })
    }

    fun subscribe(symbols: List<String>) {
        val cleaned = symbols.map { if (it.contains(":")) it.substringAfter(":") else it }
        activeSymbols.addAll(cleaned)
        sendSubscription(cleaned)
    }

    private fun sendSubscription(symbols: List<String>) {
        try {
            val subMsg = JSONObject().apply {
                put("action", "subscribe")
                put("symbols", org.json.JSONArray(symbols))
            }.toString()
            webSocket?.send(subMsg)
        } catch (_: Exception) {}
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closed")
        webSocket = null
    }
}
