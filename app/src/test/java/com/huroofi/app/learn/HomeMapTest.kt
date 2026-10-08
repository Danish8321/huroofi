package com.huroofi.app.learn

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.huroofi.app.data.content.TestContent
import com.huroofi.app.parent.LetterState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeMapTest {
    private val repo = TestContent.repo

    private fun chips(stage: Int, learned: Set<Int>) = repo.lettersInStage(stage).sortedBy { it.index }.map {
        StageChip(it, if (it.index in learned) LetterState.LEARNED else LetterState.TO_GO)
    }

    @Test
    fun everyStageFitsThePath() {
        for (stage in repo.stages) assertEquals(stage.name, HomeSpec.SLOTS.size, repo.lettersInStage(stage.stage).size)
    }

    @Test
    fun slotsStretchWithTheCard() {
        assertNear(DpOffset(90.dp, 430.dp), HomeSpec.slot(0, 340.dp, 500.dp))
        assertNear(DpOffset(55.dp, 46.dp), HomeSpec.slot(3, 170.dp, 250.dp))
    }

    private fun assertNear(expected: DpOffset, actual: DpOffset) {
        assertEquals(expected.x.value, actual.x.value, 0.01f)
        assertEquals(expected.y.value, actual.y.value, 0.01f)
    }

    @Test
    fun pictureSitsOnTheSideWithRoom() {
        assertFalse(HomeSpec.pictureOnLeft(0))
        assertTrue(HomeSpec.pictureOnLeft(1))
        assertTrue(HomeSpec.pictureOnLeft(2))
        assertFalse(HomeSpec.pictureOnLeft(3))
    }

    @Test
    fun buttonSaysWhatHappensNext() {
        assertEquals("Let's learn", homeButtonLabel(review = false, traceOnly = false))
        assertEquals("Practise", homeButtonLabel(review = true, traceOnly = false))
        assertEquals("Trace", homeButtonLabel(review = true, traceOnly = true))
    }

    @Test
    fun subtitleCountsLearnedInTheStage() {
        assertEquals("Stage 1 · ${repo.stage(1).name} · 1 of 4", homeSubtitle(repo.stage(1), chips(1, setOf(1))))
    }

    @Test
    fun mapSubtitleCountsAllLetters() {
        val stages = repo.stages.map { MapStage(it, StageState.LOCKED, chips(it.stage, setOf(1, 2))) }
        assertEquals("2 of 28 letters learned · finish one stage to unlock the next", mapSubtitle(stages))
        val open = stages.map { it.copy(state = StageState.OPEN) }
        assertEquals("2 of 28 letters learned · start any stage", mapSubtitle(open))
    }
}
