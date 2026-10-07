package com.huroofi.app.data.content

import com.huroofi.app.ui.theme.StageColors
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * These classes mirror data/letters.json exactly and are the only place its shape is declared.
 * The decoder is strict (see ContentJson), so a JSON change forces a change here in the same commit.
 */

@Serializable
data class LetterAudio(
    val letter: String,
    val word: String,
)

@Serializable
data class Letter(
    val index: Int,
    val letter: String,
    @SerialName("name_ar") val nameAr: String,
    @SerialName("name_latin") val nameLatin: String,
    @SerialName("word_ar") val wordAr: String,
    @SerialName("word_first") val wordFirst: String,
    @SerialName("word_rest") val wordRest: String,
    @SerialName("meaning_en") val meaningEn: String,
    val picture: String,
    /** False when a toddler is unlikely to know the picture word (e.g. washing machine); Find skips it. */
    @SerialName("toddler_word") val toddlerWord: Boolean,
    val stage: Int,
    @SerialName("card_image") val cardImage: String,
    val audio: LetterAudio,
) {
    /** Drawable name of the card image, e.g. `assets/cards/01_alif_lion.png` -> `card_01_alif_lion`. */
    fun cardResourceName(): String = "card_" + cardImage.substringAfterLast('/').substringBeforeLast('.')

    /** Drawable name of the picture, e.g. `lion` -> `pic_lion`. */
    fun pictureResourceName(): String = "pic_$picture"
}

@Serializable
data class Stage(
    val stage: Int,
    val name: String,
    val letters: List<String>,
    val border: String,
    val pastel: String,
    @SerialName("accent_dark") val accentDark: String,
) {
    fun colors(): StageColors = StageColors.fromHex(border, pastel, accentDark)
}

@Serializable
data class ContentFile(
    val letters: List<Letter>,
    val stages: List<Stage>,
)

/*
 * data/strokes.json: how each isolated letter is written (plan 09 decision 1).
 * A point is [x, y] as fractions (0..1) of the glyph's ink box in Noto Naskh Arabic Bold, x right, y down.
 */

@Serializable
data class TraceStroke(
    val order: Int,
    val dot: Boolean,
    val points: List<List<Float>>,
)

@Serializable
data class LetterStrokes(
    val index: Int,
    val strokes: List<TraceStroke>,
)

@Serializable
data class StrokeFile(
    val letters: List<LetterStrokes>,
)
