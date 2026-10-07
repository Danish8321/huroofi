package com.huroofi.app.data.progress

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * The preference "schema". Keys are never renamed in place: a change means a new key plus a
 * migration that reads the old one, and a bump of [CURRENT_SCHEMA_VERSION].
 */
object ProgressKeys {
    const val CURRENT_SCHEMA_VERSION = 1

    val schemaVersion = intPreferencesKey("schema_version")
    val mode = stringPreferencesKey("mode")
    val completedLetters = stringSetPreferencesKey("completed_letters")
    val stickers = stringSetPreferencesKey("stickers")
    val voiceEnabled = booleanPreferencesKey("voice_enabled")
    val harakatEnabled = booleanPreferencesKey("harakat_enabled")
    val dailyLimitMinutes = intPreferencesKey("daily_limit_minutes")
    val usageDay = stringPreferencesKey("usage_day")
    val usageSeconds = intPreferencesKey("usage_seconds")
    val offlinePackReady = booleanPreferencesKey("offline_pack_ready")

    val names: List<String> = listOf(
        schemaVersion.name, mode.name, completedLetters.name, stickers.name, voiceEnabled.name,
        harakatEnabled.name, dailyLimitMinutes.name, usageDay.name, usageSeconds.name,
        offlinePackReady.name,
    )
}

/**
 * Progress and parent settings on the device only. No identifiers, no network.
 *
 * @param stageOf letter index -> stage number, derived from letters.json (also defines which
 *   letter indexes and stages are valid).
 */
class ProgressRepository(
    private val store: DataStore<Preferences>,
    private val stageOf: Map<Int, Int>,
    private val today: () -> String = { LocalDate.now().toString() },
) {
    private val validStages = stageOf.values.toSet()

    val mode: Flow<AgeMode> = store.data.map { AgeMode.parse(it[ProgressKeys.mode]) }

    val completedLetters: Flow<Set<Int>> =
        store.data.map { p -> p[ProgressKeys.completedLetters].toIntSet().filter { it in stageOf }.toSet() }

    val stickers: Flow<Set<Int>> =
        store.data.map { p -> p[ProgressKeys.stickers].toIntSet().filter { it in validStages }.toSet() }

    val voiceEnabled: Flow<Boolean> = store.data.map { it[ProgressKeys.voiceEnabled] ?: true }

    val harakatEnabled: Flow<Boolean> = store.data.map { it[ProgressKeys.harakatEnabled] ?: false }

    val dailyLimitMinutes: Flow<Int> =
        store.data.map { clampLimitMinutes(it[ProgressKeys.dailyLimitMinutes] ?: DEFAULT_LIMIT_MINUTES) }

    val usageSecondsToday: Flow<Int> = store.data.map {
        usageForToday(it[ProgressKeys.usageDay], it[ProgressKeys.usageSeconds] ?: 0, today())
    }

    val offlinePackReady: Flow<Boolean> = store.data.map { it[ProgressKeys.offlinePackReady] ?: false }

    val unlockedStage: Flow<Int> = completedLetters.map { unlockedStage(it, stageOf) }

    val limitReached: Flow<Boolean> =
        combine(usageSecondsToday, dailyLimitMinutes) { used, limit -> limitReached(used, limit) }

    suspend fun ensureSchema() {
        store.edit { p ->
            if (p[ProgressKeys.schemaVersion] == null) p[ProgressKeys.schemaVersion] = ProgressKeys.CURRENT_SCHEMA_VERSION
        }
    }

    suspend fun setMode(mode: AgeMode) {
        store.edit { it[ProgressKeys.mode] = mode.name }
    }

    suspend fun markLetterComplete(index: Int) {
        if (index !in stageOf) return
        store.edit { p ->
            p[ProgressKeys.completedLetters] = (p[ProgressKeys.completedLetters].orEmpty()) + index.toString()
        }
    }

    suspend fun awardSticker(stage: Int) {
        if (stage !in validStages) return
        store.edit { p ->
            p[ProgressKeys.stickers] = (p[ProgressKeys.stickers].orEmpty()) + stage.toString()
        }
    }

    /** Clears learned letters and stickers, so the Map is back at stage 1. Every setting stays (plan 13 decision 2). */
    suspend fun startOver() {
        store.edit { p ->
            p.remove(ProgressKeys.completedLetters)
            p.remove(ProgressKeys.stickers)
        }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        store.edit { it[ProgressKeys.voiceEnabled] = enabled }
    }

    suspend fun setHarakatEnabled(enabled: Boolean) {
        store.edit { it[ProgressKeys.harakatEnabled] = enabled }
    }

    suspend fun setDailyLimitMinutes(minutes: Int) {
        store.edit { it[ProgressKeys.dailyLimitMinutes] = clampLimitMinutes(minutes) }
    }

    suspend fun addUsageSeconds(seconds: Int) {
        if (seconds <= 0) return
        store.edit { p ->
            val day = today()
            val current = usageForToday(p[ProgressKeys.usageDay], p[ProgressKeys.usageSeconds] ?: 0, day)
            p[ProgressKeys.usageDay] = day
            p[ProgressKeys.usageSeconds] = current + seconds
        }
    }

    suspend fun setOfflinePackReady(ready: Boolean) {
        store.edit { it[ProgressKeys.offlinePackReady] = ready }
    }

    private fun Set<String>?.toIntSet(): Set<Int> = orEmpty().mapNotNull { it.toIntOrNull() }.toSet()
}
