package com.huroofi.app.learn

import com.huroofi.app.data.content.Stage
import org.junit.Assert.assertEquals
import org.junit.Test

class StickerTest {
    @Test
    fun stickerIsNamedAfterItsStage() {
        val stage = Stage(1, "Sunny Meadow", emptyList(), "#F59E0B", "#FFEDC4", "#A35400")
        assertEquals("Sunny Meadow sticker", stickerName(stage))
    }
}
