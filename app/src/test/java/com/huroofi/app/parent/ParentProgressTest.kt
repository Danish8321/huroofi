package com.huroofi.app.parent

import com.huroofi.app.data.content.TestContent
import com.huroofi.app.data.progress.unlockedStage
import org.junit.Assert.assertEquals
import org.junit.Test

class ParentProgressTest {
    private val content = TestContent.repo
    private val stageOf = content.letters.associate { it.index to it.stage }

    private fun progress(completed: Set<Int>) =
        parentProgress(content.letters, content.stages, completed, unlockedStage(completed, stageOf))

    private fun counts(p: ParentProgress) = Triple(p.learned, p.learning, p.toGo)

    @Test
    fun nothingDoneMeansAlifIsLearning() {
        val p = progress(emptySet())
        assertEquals(Triple(0, 1, 27), counts(p))
        assertEquals(LetterState.LEARNING, p.states[1])
        assertEquals(1, p.stage.stage)
        assertEquals(0f, p.fraction)
    }

    @Test
    fun finishingStageOneOpensStageTwosFirstLetter() {
        val p = progress(setOf(1, 2, 3, 4))
        assertEquals(Triple(4, 1, 23), counts(p))
        assertEquals(LetterState.LEARNING, p.states[5])
        assertEquals(2, p.stage.stage)
    }

    @Test
    fun aGapInsideTheOpenStageIsTheLearningLetter() {
        val p = progress(setOf(1, 3))
        assertEquals(LetterState.LEARNING, p.states[2])
        assertEquals(LetterState.TO_GO, p.states[4])
        assertEquals(Triple(2, 1, 25), counts(p))
    }

    @Test
    fun lettersBeyondTheOpenStageAreNeverLearning() {
        // Letter 9 is done ahead of time; stage 1 is still open.
        val p = progress(setOf(9))
        assertEquals(LetterState.LEARNED, p.states[9])
        assertEquals(LetterState.LEARNING, p.states[1])
        assertEquals(1, p.stage.stage)
    }

    @Test
    fun allLearnedMeansNoLearningLetter() {
        val p = progress((1..28).toSet())
        assertEquals(Triple(28, 0, 0), counts(p))
        assertEquals(7, p.stage.stage)
        assertEquals(1f, p.fraction)
    }

    @Test
    fun countsAlwaysSumToTwentyEight() {
        for (n in 0..28) {
            val p = progress((1..n).toSet())
            assertEquals(28, p.learned + p.learning + p.toGo)
        }
    }
}
