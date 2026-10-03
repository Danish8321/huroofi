package com.huroofi.app.learn

import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.LetterAudio
import com.huroofi.app.data.content.Stage

/** Sample content for `@Preview` only (previews have no AppContainer). The app reads letters.json. */
internal object LearnPreviewData {
    private fun letter(index: Int, glyph: String, nameLatin: String, first: String, rest: String, meaning: String) =
        Letter(index, glyph, glyph, nameLatin, first + rest, first, rest, meaning, meaning, 1, "", LetterAudio("", ""))

    val stage = Stage(1, "Sunny Meadow", listOf("أ", "ب", "ت", "ث"), "#F59E0B", "#FFEDC4", "#A35400")
    val nextStage = Stage(2, "Carrot Farm", emptyList(), "#16A34A", "#D6F2C8", "#0F5F35")

    val letters = listOf(
        letter(1, "أ", "alif", "أ", "سد", "lion"),
        letter(2, "ب", "baa", "ب", "طة", "duck"),
        letter(3, "ت", "taa", "ت", "فاح", "apple"),
        letter(4, "ث", "thaa", "ث", "علب", "fox"),
    )
}
