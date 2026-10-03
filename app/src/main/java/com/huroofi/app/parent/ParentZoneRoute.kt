package com.huroofi.app.parent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.data.progress.AgeMode

/** Reads stored progress and shows the Parent zone. [onBack] returns to this session's home (decision 8). */
@Composable
fun ParentZoneRoute(onBack: () -> Unit, footer: @Composable ColumnScope.() -> Unit = {}) {
    val container = LocalAppContainer.current
    val content = container.content
    val progress = container.progress
    val mode by progress.mode.collectAsStateWithLifecycle(AgeMode.TODDLER)
    val completed by progress.completedLetters.collectAsStateWithLifecycle(emptySet())
    val openStage by progress.unlockedStage.collectAsStateWithLifecycle(1)
    val summary = remember(completed, openStage) {
        parentProgress(content.letters, content.stages, completed, openStage)
    }
    val grid = remember(summary) {
        content.letters.sortedBy { it.index }.map { GridLetter(it.letter, it.nameLatin, summary.states.getValue(it.index)) }
    }
    BackHandler(onBack = onBack)
    ParentZoneScreen(summary, grid, mode, onBack, footer)
}
