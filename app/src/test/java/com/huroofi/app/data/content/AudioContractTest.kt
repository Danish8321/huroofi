package com.huroofi.app.data.content

import com.huroofi.app.audio.Clips
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every clip the app can ask for has a file (placeholder or real) under assets. */
class AudioContractTest {
    private val assets = File("src/main/assets")
    private val content = ContentJson.decode(File(assets, "letters.json").readText(Charsets.UTF_8))

    @Test
    fun everyClipHasAFile() {
        val missing = Clips.all(content.letters).filterNot { File(assets, it.path).isFile }
        assertTrue("missing audio (run tools/make-placeholders.sh): ${missing.map { it.path }}", missing.isEmpty())
    }
}
