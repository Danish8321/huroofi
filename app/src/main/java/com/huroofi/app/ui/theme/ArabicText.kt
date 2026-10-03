package com.huroofi.app.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

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
        style = style.copy(
            fontSize = size,
            textDirection = TextDirection.Rtl,
            lineHeight = size * 1.5f,
        ),
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
