package com.huroofi.app.toddler

import com.huroofi.app.data.content.Letter

/**
 * Spoken-prompt captions. letters.json has no definite forms, so they are built with the article
 * "ال". Every generated phrase is listed in docs/review/toddler-phrases.md for native-speaker review.
 */
object ToddlerPhrases {
    /** "Where is the duck?": أين البطة؟ */
    fun whereIsAr(l: Letter): String = "أين ال${l.wordAr}؟"

    fun whereIsEn(l: Letter): String = "Where's the ${l.meaningEn}?"

    /** "Colour the baa": لوّن الباء */
    fun colourAr(l: Letter): String = "لوّن ال${l.nameAr}"

    fun colourEn(l: Letter): String = "Colour the letter ${l.nameLatin}"
}
