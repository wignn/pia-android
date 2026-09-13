package dev.wign.pia.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class HistoryPage(
    @Json(name = "has_more") val hasMore: Boolean = false,
    @Json(name = "items") val items: List<HistoricalCandleItem> = emptyList(),
    @Json(name = "next_before") val nextBefore: Long? = null
)

@JsonClass(generateAdapter = true)
data class HistoricalCandleItem(
    @Json(name = "time") val time: Long,
    @Json(name = "open") val open: Double,
    @Json(name = "high") val high: Double,
    @Json(name = "low") val low: Double,
    @Json(name = "close") val close: Double,
    @Json(name = "volume") val volume: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class SocialPostsResponse(
    @Json(name = "has_more") val hasMore: Boolean = false,
    @Json(name = "items") val items: List<SocialPostItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SocialPostItem(
    @Json(name = "author_username") val authorUsername: String = "",
    @Json(name = "author_display_name") val authorDisplayName: String = "",
    @Json(name = "text") val text: String = "",
    @Json(name = "url") val url: String = "",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "platform") val platform: String = "twitter",
    @Json(name = "like_count") val likeCount: Long = 0,
    @Json(name = "retweet_count") val retweetCount: Long = 0
)

class PiaApiClient(
    private val baseUrl: String = "https://api-engine.wign.dev",
    private val apiKey: String = "silvia"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val historyAdapter = moshi.adapter(HistoryPage::class.java)
    private val socialAdapter = moshi.adapter(SocialPostsResponse::class.java)

    suspend fun getHistory(
        symbol: String,
        resolution: String = "1m",
        limit: Int = 120
    ): List<Candle> = withContext(Dispatchers.IO) {
        val cleanSymbol = if (symbol.contains(":")) symbol.substringAfter(":") else symbol
        val url = "$baseUrl/api/v1/market/history/$cleanSymbol?resolution=$resolution&limit=$limit&api_key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("x-api-key", apiKey)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val bodyString = response.body?.string() ?: return@withContext emptyList()
                val page = historyAdapter.fromJson(bodyString) ?: return@withContext emptyList()

                page.items
                    .map { item ->
                        Candle(
                            symbol = symbol,
                            time = item.time,
                            open = item.open,
                            high = item.high,
                            low = item.low,
                            close = item.close,
                            volume = item.volume
                        )
                    }
                    .distinctBy { it.time }
                    .sortedBy { it.time }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSocialPosts(limit: Int = 30): List<SocialPostItem> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/api/v1/social/posts?limit=$limit&api_key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("x-api-key", apiKey)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val bodyString = response.body?.string() ?: return@withContext emptyList()
                val page = socialAdapter.fromJson(bodyString) ?: return@withContext emptyList()
                page.items
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
