package com.huroofi.app.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.huroofi.app.ui.components.windowWidth
import com.huroofi.app.ui.components.windowHeight
import com.huroofi.app.ui.components.useWideLayout
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.FillColumn
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StepTabs
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.stepTabsPairs
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.IgnoreFontScale
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.highlightedWord

/** Sizes and colours of Meet (`Lesson.html`, plan 15 decision 6). */
object LessonSpec {
    val Back = 64.dp
    val Sound = 64.dp
    val CardRing = 6.dp
    val CardCorner = 36.dp
    val CardEdge = 8.dp
    val WordCorner = 28.dp
    val Picture = 104.dp
    val PictureImage = 84.dp
    val ShapeCell = 84.dp
    val ShapeCorner = 18.dp
    val EdgeColor = Color(0xFFCFE2F7)
    val ShapeCellColor = Color(0xFFF4F7FB)
    val AloneCellColor = Color(0xFFFFEDC4)
    val ReaderChip = Color(0xFFE8E0FF)
    val ReaderChipText = Color(0xFF4C2DB0)
    const val LETTER_SP = 170f
    const val LETTER_WITH_SHAPES_SP = 140f
    /** Wide landscape tablet: the letter card is full height, so the letter grows to this. */
    const val LETTER_WIDE_SP = 260f
    val SideWidth = 380.dp
    const val NAME_SP = 22f
    const val WORD_SP = 48f
    const val SHAPE_SP = 36f
    const val INLINE_SP = 22f
    val textSizes = listOf(LETTER_SP, LETTER_WITH_SHAPES_SP, LETTER_WIDE_SP, NAME_SP, WORD_SP, SHAPE_SP, INLINE_SP)

    /** Room above and below the letter for the name row (16 dp in, 28 dp tall). */
    val LetterBand = 44.dp
    /** A short window shrinks the letter to this, then scrolls the cards. */
    const val LETTER_MIN_SP = 96f
    val CardMin = LETTER_MIN_SP.dp + LetterBand * 2

    val touchSizes = listOf(Back, Sound, Picture, HuroofiDimens.PrimaryButtonHeight)
    val colors = listOf(
        HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, HuroofiTokens.Sun, EdgeColor,
        ShapeCellColor, AloneCellColor, ReaderChip, ReaderChipText,
    )

    /** The back button and step bar, shared with Trace. */
    val headerPairs = listOf(ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "back chevron")) + stepTabsPairs

    /** 18 sp English is normal text; the Arabic is all large. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "sound icon"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "word"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "word meaning"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "in words"),
        ContrastPair(ReaderChipText, ReaderChip, large = false, "Early reader chip"),
        ContrastPair(HuroofiTokens.Navy, ShapeCellColor, large = true, "shape glyph"),
        ContrastPair(HuroofiTokens.Muted, ShapeCellColor, large = false, "shape label"),
        ContrastPair(HuroofiTokens.Navy, AloneCellColor, large = true, "alone glyph"),
        ContrastPair(HuroofiTokens.Muted, AloneCellColor, large = false, "alone label"),
    ) + headerPairs + ButtonKind.Primary.contrastPairs()

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = false, "letter name"),
        ContrastPair(HuroofiTokens.Muted, stage.pastel, large = true, "Arabic letter name"),
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = true, "big letter"),
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "word first letter"),
    )
}

val PathTabs = PathStep.entries.map { it.tab }

/** Back button and step bar, shared by Meet and Trace. */
@Composable
fun PathHeader(step: PathStep?, backLabel: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        RoundIconButton(
            contentDescription = backLabel,
            onClick = onBack,
            toddler = true,
            size = LessonSpec.Back,
            containerColor = HuroofiTokens.Card,
            shadowColor = LessonSpec.EdgeColor,
            shadowDepth = 4.dp,
        ) { ChevronIcon(HuroofiTokens.Navy, pointsRight = false, size = 32.dp) }
        // Trace practice has no path, so no step bar (plan 13 decision 4).
        if (step != null) StepTabs(PathTabs, step.ordinal, Modifier.weight(1f))
    }
}

/**
 * The letter big on its stage card, then its picture word, then (Early reader) its shapes. The sound
 * button says the name and the word; the letter says its name; the word card says the word.
 */
@Composable
fun LessonScreen(
    letter: Letter,
    stage: Stage,
    showShapes: Boolean,
    onBack: () -> Unit,
    onHear: () -> Unit,
    onHearLetter: () -> Unit,
    onHearWord: () -> Unit,
    onNext: (() -> Unit)?,
) {
    val colors = stage.colors()
    val next: @Composable () -> Unit = { if (onNext != null) PrimaryButton("Trace it", onClick = onNext, icon = ButtonIcons.Next) }
    BoxWithConstraints(Modifier.fillMaxSize().background(HuroofiTokens.Sky)) {
        if (useWideLayout(windowWidth.value, windowHeight.value)) {
            // Wide landscape tablet (`LessonTablet.html`): letter card left at full height, the rest beside it.
            Column(
                Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 28.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PathHeader(PathStep.MEET, "Back to home", onBack)
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    LetterCard(letter, colors, LessonSpec.LETTER_WIDE_SP, onHear, onHearLetter, Modifier.weight(1f).fillMaxHeight())
                    val gap = if (showShapes) 2 else 1
                    FillColumn(fill = gap, fillMin = 0.dp, modifier = Modifier.width(LessonSpec.SideWidth).fillMaxHeight(), edge = 0.dp) {
                        WordCard(letter, colors, onHearWord)
                        if (showShapes) ShapesCard(letter)
                        Spacer(Modifier)
                        next()
                    }
                }
            }
        } else {
            CappedWidth(HuroofiTokens.Sky) {
                Column(
                    Modifier.fillMaxSize().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 26.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PathHeader(PathStep.MEET, "Back to home", onBack)
                    // The header and "Trace it" stay put; a short window shrinks the letter card, then scrolls.
                    FillColumn(fill = 0, fillMin = LessonSpec.CardMin, modifier = Modifier.weight(1f)) {
                        val full = if (showShapes) LessonSpec.LETTER_WITH_SHAPES_SP else LessonSpec.LETTER_SP
                        LetterCard(letter, colors, full, onHear, onHearLetter, Modifier)
                        WordCard(letter, colors, onHearWord)
                        if (showShapes) ShapesCard(letter)
                    }
                    next()
                }
            }
        }
    }
}

@Composable
private fun LetterCard(letter: Letter, colors: StageColors, full: Float, onHear: () -> Unit, onHearLetter: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(LessonSpec.CardCorner)
    val letterWiggle = rememberWiggle()
    Box(
        modifier
            .fillMaxWidth()
            .dropEdge(colors.accent, LessonSpec.CardEdge, shape)
            .background(colors.pastel, shape)
            .border(LessonSpec.CardRing, colors.border, shape),
    ) {
        Row(
            Modifier.padding(start = 20.dp + LessonSpec.CardRing, top = 16.dp + LessonSpec.CardRing).clearAndSetSemantics {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(letter.nameLatin, style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
            ArabicText(letter.nameAr, size = LessonSpec.NAME_SP.sp, color = HuroofiTokens.Muted)
        }
        // The whole card is the letter's button, so a thin letter like alif is still easy to tap.
        // No clip and no ripple: the glyph's line box overflows, and the wiggle is the answer.
        BoxWithConstraints(
            Modifier
                .matchParentSize()
                .clickable(interactionSource = null, indication = null, role = Role.Button) {
                    letterWiggle.play()
                    onHearLetter()
                }
                .clearAndSetSemantics {
                    contentDescription = "Letter ${letter.nameLatin}. Tap to hear"
                    role = Role.Button
                    onClick {
                        letterWiggle.play()
                        onHearLetter()
                        true
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            // A short window shrinks the card (the prototype's `min-height: 0`) and the letter with it:
            // Naskh ink is at most about 1 em tall, so the em is the card's height less a band for the name row.
            val cardHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }
            // CenteredLetter ignores the font scale, so 1 sp is 1 dp here.
            val size = minOf(full, (cardHeight - LessonSpec.LetterBand * 2).value).coerceAtLeast(LessonSpec.LETTER_MIN_SP)
            CenteredLetter(letter.letter, Modifier.wiggle(letterWiggle), size = size.sp, color = HuroofiTokens.Navy)
        }
        RoundIconButton(
            contentDescription = "Hear ${letter.nameLatin} and ${letter.meaningEn}",
            onClick = onHear,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 16.dp + LessonSpec.CardRing, end = 16.dp + LessonSpec.CardRing),
            toddler = true,
            size = LessonSpec.Sound,
            containerColor = HuroofiTokens.Sun,
            shadowColor = HuroofiTokens.SunShadow,
        ) { SoundIcon(HuroofiTokens.Navy, size = 30.dp) }
    }
}

@Composable
private fun WordCard(letter: Letter, colors: StageColors, onHearWord: () -> Unit) {
    val shape = RoundedCornerShape(LessonSpec.WordCorner)
    val pictureWiggle = rememberWiggle()
    Row(
        Modifier
            .fillMaxWidth()
            .dropEdge(LessonSpec.EdgeColor, 5.dp, shape)
            .background(HuroofiTokens.Card, shape)
            .clickable(interactionSource = null, indication = null, role = Role.Button) {
                pictureWiggle.play()
                onHearWord()
            }
            .clearAndSetSemantics {
                contentDescription = "${letter.meaningEn.replaceFirstChar { it.uppercase() }}. Tap to hear"
                role = Role.Button
                onClick {
                    pictureWiggle.play()
                    onHearWord()
                    true
                }
            }
            .padding(start = 10.dp, end = 18.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(LessonSpec.Picture).background(HuroofiTokens.Sky, RoundedCornerShape(22.dp)).wiggle(pictureWiggle),
            contentAlignment = Alignment.Center,
        ) {
            Image(letterPicture(letter), contentDescription = null, modifier = Modifier.size(LessonSpec.PictureImage))
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            ArabicText(highlightedWord(letter.wordFirst, letter.wordRest, colors.accent), size = LessonSpec.WORD_SP.sp, color = HuroofiTokens.Navy)
            Text(letter.meaningEn, style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Muted)
        }
    }
}

/** "ب in words": Alone / Start / Middle / End, right to left; non-joining letters show two (plan 07 decision 2). */
@Composable
private fun ShapesCard(letter: Letter) {
    Column(
        Modifier.fillMaxWidth().background(HuroofiTokens.Card, RoundedCornerShape(26.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ArabicText(letter.letter, size = LessonSpec.INLINE_SP.sp, color = HuroofiTokens.Navy)
                Text("in words", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
            }
            Text(
                "Early reader",
                Modifier.background(LessonSpec.ReaderChip, CircleShape).padding(horizontal = 12.dp, vertical = 2.dp),
                style = HuroofiText.caption.copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold),
                color = LessonSpec.ReaderChipText,
            )
        }
        val shapes = letterShapes(letter.letter)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for ((kind, text) in shapes) {
                    val alone = kind == ShapeKind.ALONE
                    Column(
                        Modifier
                            .weight(1f)
                            .heightIn(min = LessonSpec.ShapeCell) // grows with a large text size rather than clipping its label
                            .background(if (alone) LessonSpec.AloneCellColor else LessonSpec.ShapeCellColor, RoundedCornerShape(LessonSpec.ShapeCorner))
                            .clearAndSetSemantics { contentDescription = "${kind.label} shape" },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        IgnoreFontScale { ArabicText(text, size = LessonSpec.SHAPE_SP.sp, color = HuroofiTokens.Navy) }
                        Text(kind.label, style = HuroofiText.caption.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = HuroofiTokens.Muted)
                    }
                }
                // Keep cells the same width when a non-joining letter has only two.
                repeat(ShapeKind.entries.size - shapes.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun LessonReaderPreview() {
    HuroofiTheme {
        LessonScreen(LearnPreviewData.letters[1], LearnPreviewData.stage, showShapes = true, {}, {}, {}, {}, onNext = {})
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun LessonPreschoolPreview() {
    HuroofiTheme {
        LessonScreen(LearnPreviewData.letters[0], LearnPreviewData.stage, showShapes = false, {}, {}, {}, {}, onNext = {})
    }
}
