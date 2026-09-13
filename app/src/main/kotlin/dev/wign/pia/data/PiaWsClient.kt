package dev.wign.pia.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
import java.util.concurrent.TimeUnit

class PiaWsClient(
    private val baseUrl: String = "wss://api-engine.wign.dev/api/v1/ws",
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val tickAdapter = moshi.adapter(MarketTick::class.java)
    private val subAdapter = moshi.adapter(WsSubscription::class.java)

    private var webSocket: WebSocket? = null
    private val _ticks = MutableSharedFlow<MarketTick>(extraBufferCapacity = 512)
    val ticks: SharedFlow<MarketTick> = _ticks.asSharedFlow()

    private val activeChannels = mutableSetOf<String>()

    fun connect(ticket: String? = null) {
        val url = if (!ticket.isNullOrBlank()) "$baseUrl?ticket=$ticket" else baseUrl
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Resubscribe to channels on open
                if (activeChannels.isNotEmpty()) {
                    val sub = WsSubscription(channels = activeChannels.toList())
                    webSocket.send(subAdapter.toJson(sub))
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val tick = tickAdapter.fromJson(text)
                    if (tick != null) {
                        scope.launch {
                            _ticks.emit(tick)
                        }
                    }
                } catch (_: Exception) {
                    // Ignore non-tick messages or ping frames
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Scheduled reconnect can be handled here
            }
        })
    }

    fun subscribe(channels: List<String>) {
        activeChannels.addAll(channels)
        val sub = WsSubscription(channels = channels)
        webSocket?.send(subAdapter.toJson(sub))
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closed")
        webSocket = null
    }
}
