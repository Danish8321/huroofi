package com.huroofi.app.data.content

import com.huroofi.app.ui.theme.StageColors

/** Reads a text asset by path relative to the assets folder. A fake is used in tests. */
interface AssetReader {
    fun read(path: String): String
}

/** Letters and stages, in the order given by letters.json, and their strokes from strokes.json. No letter list lives in code. */
class ContentRepository(
    val letters: List<Letter>,
    val stages: List<Stage>,
    private val strokesByIndex: Map<Int, List<TraceStroke>> = emptyMap(),
) {
    fun letter(index: Int): Letter = letters.first { it.index == index }

    fun stage(number: Int): Stage = stages.first { it.stage == number }

    fun lettersInStage(number: Int): List<Letter> = letters.filter { it.stage == number }

    fun stageColors(letter: Letter): StageColors = stage(letter.stage).colors()

    /** The letter's strokes in writing order; empty when strokes.json has none for it. */
    fun strokes(index: Int): List<TraceStroke> = strokesByIndex[index].orEmpty()

    companion object {
        const val ASSET_PATH = "letters.json"
        const val STROKES_PATH = "strokes.json"

        fun load(reader: AssetReader): ContentRepository {
            val file = ContentJson.decode(reader.read(ASSET_PATH))
            val strokes = ContentJson.decodeStrokes(reader.read(STROKES_PATH))
            return ContentRepository(file.letters, file.stages, strokes.letters.associate { l -> l.index to l.strokes.sortedBy { it.order } })
        }
    }
}
