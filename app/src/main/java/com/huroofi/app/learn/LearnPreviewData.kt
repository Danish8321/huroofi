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

    /** Seven stages with distinct colours, for sticker and map previews. */
    val stages = listOf(
        stage,
        nextStage,
        Stage(3, "Flower Hill", emptyList(), "#14A88E", "#D2F4EC", "#00695C"),
        Stage(4, "Rocket Base", emptyList(), "#7C5CE0", "#E8E0FF", "#4C2DB0"),
        Stage(5, "Birdy Forest", emptyList(), "#2F80ED", "#DAEAFF", "#0B4FB0"),
        Stage(6, "Pencil Town", emptyList(), "#4CAF3A", "#DFF5D5", "#1F6E1A"),
        Stage(7, "Star Castle", emptyList(), "#F2643A", "#FFE2D7", "#B2340E"),
    )

    val letters = listOf(
        letter(1, "أ", "alif", "أ", "سد", "lion"),
        letter(2, "ب", "baa", "ب", "طة", "duck"),
        letter(3, "ت", "taa", "ت", "فاح", "apple"),
        letter(4, "ث", "thaa", "ث", "علب", "fox"),
    )
}
