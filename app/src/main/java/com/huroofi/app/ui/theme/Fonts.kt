package com.huroofi.app.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.huroofi.app.R

@OptIn(ExperimentalTextApi::class)
private fun variable(resId: Int, weight: Int) = Font(
    resId = resId,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

/** English text. Variable font, weights 500-800 (HANDOFF section 5). */
val BalooBhaijaan2 = FontFamily(
    listOf(500, 600, 700, 800).map { variable(R.font.baloo_bhaijaan_2, it) },
)

/** Every Arabic string. Bold (700) only. */
val NotoNaskhArabic = FontFamily(variable(R.font.noto_naskh_arabic, 700))
