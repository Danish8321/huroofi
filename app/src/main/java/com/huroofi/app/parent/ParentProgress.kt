package com.huroofi.app.parent

import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage

/** How a letter shows in the Parent zone (glossary: Learned letter, Learning letter, Letters to go). */
enum class LetterState { LEARNED, LEARNING, TO_GO }

/** What the Parent zone card and grid show (plan 06 decision 2). [states] is keyed by letter index. */
data class ParentProgress(val stage: Stage, val stageCount: Int, val states: Map<Int, LetterState>) {
    val learned: Int get() = states.values.count { it == LetterState.LEARNED }
    val learning: Int get() = states.values.count { it == LetterState.LEARNING }
    val toGo: Int get() = states.values.count { it == LetterState.TO_GO }

    /** Share of all letters learned, 0..1. */
    val fraction: Float get() = if (states.isEmpty()) 0f else learned.toFloat() / states.size
}

/**
 * Learned = lesson completed. Learning = the first letter of [openStage] that is not learned, or none.
 * Everything else is to go.
 */
fun parentProgress(letters: List<Letter>, stages: List<Stage>, completed: Set<Int>, openStage: Int): ParentProgress {
    val learning = letters.sortedBy { it.index }.firstOrNull { it.stage == openStage && it.index !in completed }?.index
    val states = letters.associate { letter ->
        letter.index to when (letter.index) {
            in completed -> LetterState.LEARNED
            learning -> LetterState.LEARNING
            else -> LetterState.TO_GO
        }
    }
    val stage = stages.first { it.stage == openStage }
    return ParentProgress(stage, stages.size, states)
}
