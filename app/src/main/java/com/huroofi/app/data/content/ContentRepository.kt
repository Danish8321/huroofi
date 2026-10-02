package com.huroofi.app.data.content

import com.huroofi.app.ui.theme.StageColors

/** Reads a text asset by path relative to the assets folder. A fake is used in tests. */
interface AssetReader {
    fun read(path: String): String
}

/** Letters and stages, in the order given by letters.json. No letter list lives in code. */
class ContentRepository(
    val letters: List<Letter>,
    val stages: List<Stage>,
) {
    fun letter(index: Int): Letter = letters.first { it.index == index }

    fun stage(number: Int): Stage = stages.first { it.stage == number }

    fun lettersInStage(number: Int): List<Letter> = letters.filter { it.stage == number }

    fun stageColors(letter: Letter): StageColors = stage(letter.stage).colors()

    companion object {
        const val ASSET_PATH = "letters.json"

        fun load(reader: AssetReader): ContentRepository {
            val file = ContentJson.decode(reader.read(ASSET_PATH))
            return ContentRepository(file.letters, file.stages)
        }
    }
}
