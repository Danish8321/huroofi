package com.huroofi.app.ui.components

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import org.junit.Assert.assertEquals
import org.junit.Test

class BuzzTest {
    private class Recorder : HapticFeedback {
        val types = mutableListOf<HapticFeedbackType>()
        override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
            types += hapticFeedbackType
        }
    }

    @Test
    fun aFinishedStrokeTicksAndARightAnswerConfirms() {
        val r = Recorder()
        val buzz = Buzz(r, enabled = true)
        buzz.tick()
        buzz.confirm()
        assertEquals(listOf(HapticFeedbackType.SegmentTick, HapticFeedbackType.Confirm), r.types)
    }

    @Test
    fun theParentSwitchOffMeansNoBuzz() {
        val r = Recorder()
        val buzz = Buzz(r, enabled = false)
        buzz.tick()
        buzz.confirm()
        assertEquals(emptyList<HapticFeedbackType>(), r.types)
    }
}
