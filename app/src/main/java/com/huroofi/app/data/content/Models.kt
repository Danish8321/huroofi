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
