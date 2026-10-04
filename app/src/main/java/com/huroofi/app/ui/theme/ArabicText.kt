package com.huroofi.app.ui.theme

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * One Arabic run: right-to-left paragraph direction and Naskh Bold, inside an otherwise
 * left-to-right layout. The surrounding layout is never flipped.
 */
@Composable
fun ArabicText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = TypeScale.ArabicChip.default.sp,
    color: Color = LocalContentColor.current,
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle = TextStyle(fontFamily = NotoNaskhArabic),
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        style = arabicStyle(style, size),
    )
}

/** The style a plain [ArabicText] run is drawn with. */
private fun arabicStyle(style: TextStyle, size: TextUnit): TextStyle =
    style.copy(fontSize = size, textDirection = TextDirection.Rtl, lineHeight = size * 1.5f)

/**
 * One letter in a tile, placed by its drawn ink rather than the font's line box. Naskh sets each
 * letter at a different height in that box, so line-box centring leaves ر س م ي low and lets ب's
 * dot leave its tile. Put it in a Box with `contentAlignment = Alignment.Center`.
 */
@Composable
fun CenteredLetter(
    letter: String,
    modifier: Modifier = Modifier,
    size: TextUnit = TypeScale.ArabicChip.default.sp,
    color: Color = LocalContentColor.current,
    style: TextStyle = TextStyle(fontFamily = NotoNaskhArabic),
) {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    // Ink is measured once per letter, size and density, not per frame.
    val shift = remember(letter, size, style, density) {
        val layout = measurer.measure(letter, arabicStyle(style, size).copy(textAlign = TextAlign.Center))
        inkBounds(layout, density)?.let { inkShift(layout.size, it) } ?: Offset.Zero
    }
    ArabicText(
        letter,
        modifier.wrapContentSize(unbounded = true).offset { IntOffset(shift.x.roundToInt(), shift.y.roundToInt()) },
        size = size,
        color = color,
        style = style,
    )
}

/** [ArabicText] for a run with styled parts, e.g. a word whose first letter is highlighted. */
@Composable
fun ArabicText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    size: TextUnit = TypeScale.ArabicChip.default.sp,
    color: Color = LocalContentColor.current,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        style = TextStyle(
            fontFamily = NotoNaskhArabic,
            fontSize = size,
            textDirection = TextDirection.Rtl,
            lineHeight = size * 1.3f,
        ),
    )
}

/** The picture word with its first letter in [highlight] (HANDOFF section 4: word_first + word_rest). */
fun highlightedWord(first: String, rest: String, highlight: Color): AnnotatedString =
    buildAnnotatedString {
        pushStyle(SpanStyle(color = highlight))
        append(first)
        pop()
        append(rest)
    }
