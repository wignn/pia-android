package dev.wign.pia.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pia_terminal_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val KEY_LAST_SYMBOL = stringPreferencesKey("last_symbol")
        val KEY_LAST_TIMEFRAME = stringPreferencesKey("last_timeframe")
        val KEY_CHART_TYPE = stringPreferencesKey("chart_type")
        val KEY_SHOW_EMA = booleanPreferencesKey("show_ema")
        val KEY_SHOW_RSI = booleanPreferencesKey("show_rsi")
        val KEY_SHOW_VOLUME = booleanPreferencesKey("show_volume")
        val KEY_SHOW_TAPE = booleanPreferencesKey("show_tape")
        val KEY_FAVORITES = stringSetPreferencesKey("favorite_symbols")
    }

    val lastSymbol: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_SYMBOL] ?: "BINANCE:BTCUSDT"
    }

    val lastTimeframe: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_TIMEFRAME] ?: "1m"
    }

    val chartType: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CHART_TYPE] ?: "candles"
    }

    val showEma: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOW_EMA] ?: true
    }

    val showRsi: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOW_RSI] ?: false
    }

    val showVolume: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOW_VOLUME] ?: true
    }

    val showTape: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOW_TAPE] ?: true
    }

    val favorites: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_FAVORITES] ?: setOf("BINANCE:BTCUSDT", "IDX:BBCA", "CAPITALCOM:GOLD")
    }

    suspend fun saveSymbol(symbol: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_SYMBOL] = symbol
        }
    }

    suspend fun saveTimeframe(timeframe: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_TIMEFRAME] = timeframe
        }
    }

    suspend fun saveChartType(type: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CHART_TYPE] = type
        }
    }

    suspend fun saveShowEma(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOW_EMA] = show
        }
    }

    suspend fun saveShowRsi(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOW_RSI] = show
        }
    }

    suspend fun saveShowVolume(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOW_VOLUME] = show
        }
    }

    suspend fun saveShowTape(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOW_TAPE] = show
        }
    }

    suspend fun toggleFavorite(symbol: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITES] ?: setOf("BINANCE:BTCUSDT", "IDX:BBCA", "CAPITALCOM:GOLD")
            val next = current.toMutableSet()
            if (next.contains(symbol)) {
                next.remove(symbol)
            } else {
                next.add(symbol)
            }
            prefs[KEY_FAVORITES] = next
        }
    }
}
