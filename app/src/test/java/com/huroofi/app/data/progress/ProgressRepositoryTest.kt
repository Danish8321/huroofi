package com.huroofi.app.data.progress

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressRepositoryTest {
    private var day = "2026-10-01"

    // 2 stages of 4 letters
    private val stageOf = (1..8).associateWith { (it - 1) / 4 + 1 }

    private fun store(): DataStore<Preferences> = InMemoryPreferencesStore()

    private fun repo(ds: DataStore<Preferences> = store()) =
        ProgressRepository(ds, stageOf, today = { day })

    @Test
    fun defaultsMatchThePlan() = runTest {
        val r = repo()
        assertEquals(AgeMode.TODDLER, r.mode.first())
        assertEquals(emptySet<Int>(), r.completedLetters.first())
        assertEquals(emptySet<Int>(), r.stickers.first())
        assertTrue(r.voiceEnabled.first())
        assertFalse(r.harakatEnabled.first())
        assertEquals(20, r.dailyLimitMinutes.first())
        assertEquals(0, r.usageSecondsToday.first())
        assertFalse(r.offlinePackReady.first())
        assertEquals(1, r.unlockedStage.first())
        assertFalse(r.limitReached.first())
    }

    @Test
    fun aNewRepositoryOverTheSameStoreSeesEarlierWrites() = runTest {
        val ds = store()
        val first = repo(ds)
        first.setMode(AgeMode.READER)
        first.setVoiceEnabled(false)
        first.setHarakatEnabled(true)
        first.markLetterComplete(3)
        first.setOfflinePackReady(true)

        val reopened = repo(ds)
        assertEquals(AgeMode.READER, reopened.mode.first())
        assertFalse(reopened.voiceEnabled.first())
        assertTrue(reopened.harakatEnabled.first())
        assertEquals(setOf(3), reopened.completedLetters.first())
        assertTrue(reopened.offlinePackReady.first())
    }

    @Test
    fun startOverClearsLettersAndStickersButKeepsSettings() = runTest {
        val r = repo()
        r.setMode(AgeMode.READER)
        r.setDailyLimitMinutes(30)
        r.addUsageSeconds(90)
        (1..4).forEach { r.markLetterComplete(it) }
        r.awardSticker(1)

        r.startOver()

        assertEquals(emptySet<Int>(), r.completedLetters.first())
        assertEquals(emptySet<Int>(), r.stickers.first())
        assertEquals(1, r.unlockedStage.first())
        assertEquals(AgeMode.READER, r.mode.first())
        assertEquals(30, r.dailyLimitMinutes.first())
        assertEquals(90, r.usageSecondsToday.first())
    }

    @Test
    fun completingAStageUnlocksTheNextAndKeepsSetSemantics() = runTest {
        val r = repo()
        (1..4).forEach { r.markLetterComplete(it) }
        r.markLetterComplete(4)
        assertEquals(setOf(1, 2, 3, 4), r.completedLetters.first())
        assertEquals(2, r.unlockedStage.first())
    }

    @Test
    fun unknownLetterAndStageAreIgnored() = runTest {
        val r = repo()
        r.markLetterComplete(99)
        r.markLetterComplete(0)
        r.awardSticker(9)
        assertEquals(emptySet<Int>(), r.completedLetters.first())
        assertEquals(emptySet<Int>(), r.stickers.first())
        r.awardSticker(2)
        assertEquals(setOf(2), r.stickers.first())
    }

    @Test
    fun limitIsClampedAtTheWriteBoundary() = runTest {
        val r = repo()
        r.setDailyLimitMinutes(3)
        assertEquals(5, r.dailyLimitMinutes.first())
        r.setDailyLimitMinutes(500)
        assertEquals(60, r.dailyLimitMinutes.first())
        r.setDailyLimitMinutes(33)
        assertEquals(35, r.dailyLimitMinutes.first())
    }

    @Test
    fun usageAccumulatesThenRollsOverNextDay() = runTest {
        val r = repo()
        r.addUsageSeconds(600)
        r.addUsageSeconds(300)
        assertEquals(900, r.usageSecondsToday.first())
        assertFalse(r.limitReached.first())
        r.addUsageSeconds(300)
        assertTrue(r.limitReached.first())

        day = "2026-10-02"
        assertEquals(0, r.usageSecondsToday.first())
        assertFalse(r.limitReached.first())
        r.addUsageSeconds(60)
        assertEquals(60, r.usageSecondsToday.first())
    }

    @Test
    fun negativeUsageIsIgnored() = runTest {
        val r = repo()
        r.addUsageSeconds(-50)
        assertEquals(0, r.usageSecondsToday.first())
    }

    @Test
    fun corruptModeFallsBackToToddler() = runTest {
        val ds = store()
        ds.edit { it[stringPreferencesKey("mode")] = "NOPE" }
        assertEquals(AgeMode.TODDLER, repo(ds).mode.first())
    }

    @Test
    fun schemaVersionIsWrittenOnceAndKeysAreStable() = runTest {
        val ds = store()
        val r = repo(ds)
        r.ensureSchema()
        r.ensureSchema()
        val prefs = ds.data.first()
        assertEquals(1, prefs[ProgressKeys.schemaVersion])
        assertEquals(
            listOf(
                "schema_version", "mode", "completed_letters", "stickers", "voice_enabled",
                "harakat_enabled", "daily_limit_minutes", "usage_day", "usage_seconds",
                "offline_pack_ready", "unlock_all", "trace_only",
            ),
            ProgressKeys.names,
        )
    }
}
