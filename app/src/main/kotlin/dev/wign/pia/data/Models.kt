package dev.wign.pia.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketTick(
    @Json(name = "symbol") val symbol: String,
    @Json(name = "price") val price: Double,
    @Json(name = "volume") val volume: Double = 0.0,
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class WsSubscription(
    @Json(name = "action") val action: String = "subscribe",
    @Json(name = "channels") val channels: List<String>
)

@JsonClass(generateAdapter = true)
data class Candle(
    @Json(name = "symbol") val symbol: String,
    @Json(name = "time") val time: Long,
    @Json(name = "open") val open: Double,
    @Json(name = "high") val high: Double,
    @Json(name = "low") val low: Double,
    @Json(name = "close") val close: Double,
    @Json(name = "volume") val volume: Double
)

@JsonClass(generateAdapter = true)
data class PriceAlert(
    @Json(name = "id") val id: String = java.util.UUID.randomUUID().toString(),
    @Json(name = "symbol") val symbol: String,
    @Json(name = "target_price") val targetPrice: Double,
    @Json(name = "condition") val condition: String,
    @Json(name = "is_triggered") var isTriggered: Boolean = false
)
