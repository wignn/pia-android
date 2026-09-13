package dev.wign.pia.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object NativeBridge {
    private var isLoaded = false
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val candleAdapter = moshi.adapter(Candle::class.java)

    init {
        try {
            System.loadLibrary("pia_mobile_core")
            isLoaded = true
        } catch (e: UnsatisfiedLinkError) {
            // Fallback for previews or systems without NDK binary
            isLoaded = false
        }
    }

    fun isNativeReady(): Boolean = isLoaded

    external fun initConflator(timeframeSec: Long)
    private external fun processTick(symbol: String, price: Double, volume: Double, timestampMs: Long): String
    external fun calculateEma(period: Int, price: Double): Double
    external fun calculateRsi(period: Int, price: Double): Double

    fun conflateTick(symbol: String, price: Double, volume: Double, timestampMs: Long): Candle? {
        if (!isLoaded) {
            return Candle(symbol, timestampMs / 1000, price, price, price, price, volume)
        }
        val json = processTick(symbol, price, volume, timestampMs)
        return try {
            candleAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
}
