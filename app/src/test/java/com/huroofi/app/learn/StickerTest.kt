package com.huroofi.app.learn

import com.huroofi.app.data.content.Stage
import org.junit.Assert.assertEquals
import org.junit.Test

class StickerTest {
    @Test
    fun stickerIsNamedAfterItsStage() {
        val stage = Stage(1, "Sunny Meadow", emptyList(), "#F59E0B", "#FFEDC4", "#A35400", "lion")
        assertEquals("Sunny Meadow sticker", stickerName(stage))
    }

    @Test
    fun bookCountsEarnedOfAll() {
        assertEquals("2 of 7 stickers · finish a stage to earn one", stickerCount(2, 7))
    }
}
