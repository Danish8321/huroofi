package com.huroofi.app.parent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.data.progress.AgeMode
import kotlinx.coroutines.launch

/** Reads stored progress and shows the Parent zone. [onBack] returns to this session's home (decision 8). */
@Composable
fun ParentZoneRoute(onBack: () -> Unit, footer: @Composable ColumnScope.() -> Unit = {}) {
    val container = LocalAppContainer.current
    val content = container.content
    val progress = container.progress
    val mode by progress.mode.collectAsStateWithLifecycle(AgeMode.TODDLER)
    val completed by progress.completedLetters.collectAsStateWithLifecycle(emptySet())
    val voice by progress.voiceEnabled.collectAsStateWithLifecycle(true)
    val harakat by progress.harakatEnabled.collectAsStateWithLifecycle(false)
    val openStage by progress.unlockedStage.collectAsStateWithLifecycle(1)
    val summary = remember(completed, openStage) {
        parentProgress(content.letters, content.stages, completed, openStage)
    }
    val grid = remember(summary) {
        content.letters.sortedBy { it.index }.map { GridLetter(it.letter, it.nameLatin, summary.states.getValue(it.index)) }
    }
    val scope = rememberCoroutineScope()
    BackHandler(onBack = onBack)
    ParentZoneScreen(
        summary,
        grid,
        mode,
        onBack = onBack,
        onModeChange = { scope.launch { progress.setMode(it) } },
        voice = voice,
        onVoiceChange = { scope.launch { progress.setVoiceEnabled(it) } },
        harakat = harakat,
        onHarakatChange = { scope.launch { progress.setHarakatEnabled(it) } },
        footer = footer,
    )
}
