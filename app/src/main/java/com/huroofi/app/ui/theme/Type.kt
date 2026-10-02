package com.huroofi.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Type scale from HANDOFF section 5, in sp so the system font scale applies.
 * Nothing may go below [FLOOR_SP]; TypeScaleTest enforces it over [all].
 */
object TypeScale {
    const val FLOOR_SP = 16f

    data class Entry(val name: String, val min: Float, val max: Float, val default: Float)

    private fun fixed(name: String, sp: Float) = Entry(name, sp, sp, sp)

    val Caption = fixed("caption", 16f)
    val Body = fixed("body", 18f)
    val SectionHeading = fixed("sectionHeading", 20f)
    val ButtonSecondary = fixed("buttonSecondary", 20f)
    val ButtonPrimary = fixed("buttonPrimary", 22f)
    val ScreenTitle = Entry("screenTitle", 26f, 36f, 32f)
    val ArabicChip = Entry("arabicChip", 26f, 34f, 30f)
    val LessonLetter = fixed("lessonLetter", 170f)
    val ToddlerSentence = Entry("toddlerSentence", 34f, 42f, 38f)
    val ToddlerSentenceTablet = fixed("toddlerSentenceTablet", 60f)
    val ToddlerWord = Entry("toddlerWord", 50f, 88f, 64f)
    val ToddlerCardLetter = fixed("toddlerCardLetter", 110f)

    val all = listOf(
        Caption, Body, SectionHeading, ButtonSecondary, ButtonPrimary, ScreenTitle, ArabicChip,
        LessonLetter, ToddlerSentence, ToddlerSentenceTablet, ToddlerWord, ToddlerCardLetter,
    )
}

/** Arabic is always one step larger than the English around it. */
fun arabicSizeNextTo(englishSp: Float): Float = englishSp + 2f

private fun english(sp: Float, weight: FontWeight) =
    TextStyle(fontFamily = BalooBhaijaan2, fontSize = sp.sp, fontWeight = weight)

private fun arabic(sp: Float) =
    TextStyle(fontFamily = NotoNaskhArabic, fontSize = sp.sp, fontWeight = FontWeight.Bold)

/** Named text styles. English uses Baloo Bhaijaan 2, Arabic uses Noto Naskh Arabic Bold. */
object HuroofiText {
    val caption = english(TypeScale.Caption.default, FontWeight.SemiBold)
    val body = english(TypeScale.Body.default, FontWeight.Medium)
    val sectionHeading = english(TypeScale.SectionHeading.default, FontWeight.ExtraBold)
    val buttonSecondary = english(TypeScale.ButtonSecondary.default, FontWeight.ExtraBold)
    val buttonPrimary = english(TypeScale.ButtonPrimary.default, FontWeight.ExtraBold)
    val screenTitle = english(TypeScale.ScreenTitle.default, FontWeight.ExtraBold)

    val arabicChip = arabic(TypeScale.ArabicChip.default)
    val arabicBody = arabic(arabicSizeNextTo(TypeScale.Body.default))
    val lessonLetter = arabic(TypeScale.LessonLetter.default)
    val toddlerSentence = arabic(TypeScale.ToddlerSentence.default)
    val toddlerSentenceTablet = arabic(TypeScale.ToddlerSentenceTablet.default)
    val toddlerWord = arabic(TypeScale.ToddlerWord.default)
    val toddlerCardLetter = arabic(TypeScale.ToddlerCardLetter.default)
}

fun huroofiTypography(): Typography = Typography().copy(
    displayLarge = HuroofiText.screenTitle,
    headlineLarge = HuroofiText.screenTitle,
    headlineMedium = HuroofiText.sectionHeading,
    titleLarge = HuroofiText.sectionHeading,
    titleMedium = HuroofiText.buttonSecondary,
    titleSmall = HuroofiText.caption,
    bodyLarge = HuroofiText.body,
    bodyMedium = HuroofiText.body,
    bodySmall = HuroofiText.caption,
    labelLarge = HuroofiText.buttonPrimary,
    labelMedium = HuroofiText.caption,
    labelSmall = HuroofiText.caption,
)
