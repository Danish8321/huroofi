package com.huroofi.app.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
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
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StepTabs
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.components.stepTabsPairs
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.IgnoreFontScale
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.highlightedWord

/** Sizes and colours of Meet (`Lesson.html`, plan 07 decisions 2 and 9). */
object LessonSpec {
    val Back = 64.dp
    val Sound = 72.dp
    val Ring = 8.dp
    val OuterCorner = 34.dp
    val InnerCorner = 28.dp
    val IndexCircle = 34.dp
    val Picture = 104.dp
    val LetterBox = 220.dp

    /** Early reader: a smaller big letter leaves room for the shapes row above "Next" on a phone. */
    val LetterBoxWithShapes = 146.dp

    /** Spare height on a tall screen goes to the letter box; the letter grows with it up to this much. */
    const val MAX_LETTER_GROWTH = 1.6f
    val ShapeCorner = 18.dp
    val EdgeColor = Color(0xFFCFE2F7)
    const val LETTER_SP = 170f
    const val LETTER_WITH_SHAPES_SP = 120f
    const val NAME_SP = 30f
    const val WORD_SP = 46f
    const val SHAPE_SP = 38f
    const val INLINE_SP = 24f
    val textSizes = listOf(LETTER_SP, LETTER_WITH_SHAPES_SP, NAME_SP, WORD_SP, SHAPE_SP, INLINE_SP)

    val touchSizes = listOf(Back, Sound, Picture)
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, HuroofiTokens.Sun, EdgeColor)

    /** The back button and step tabs, shared with Trace. */
    val headerPairs = listOf(ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "back chevron")) + stepTabsPairs

    /** "Say it with me!" is 22 sp ExtraBold, so large; 18 sp English is normal. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "letter name"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "big letter"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "sound icon"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "Say it with me!"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "Tap to hear again"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "shapes heading letter"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "shapes heading"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "shape glyph"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "shape label"),
    ) + headerPairs + ButtonKind.Primary.contrastPairs()

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = false, "letter index number"),
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = true, "Arabic letter name"),
        ContrastPair(stage.accent, stage.pastel, large = true, "word first letter"),
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = true, "word"),
        ContrastPair(HuroofiTokens.Muted, stage.pastel, large = false, "word meaning"),
    )
}

val PathTabs = PathStep.entries.map { it.tab }

/** Back button and step tabs, shared by Meet, Trace and Play. */
@Composable
fun PathHeader(step: PathStep?, backLabel: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        RoundIconButton(
            contentDescription = backLabel,
            onClick = onBack,
            toddler = true,
            size = LessonSpec.Back,
            containerColor = HuroofiTokens.Card,
            shadowColor = LessonSpec.EdgeColor,
            shadowDepth = 4.dp,
        ) { ChevronIcon(HuroofiTokens.Navy, pointsRight = false, size = 32.dp) }
        // Trace practice has no path, so no step track (plan 13 decision 4).
        if (step != null) StepTabs(PathTabs, step.ordinal, Modifier.weight(1f))
    }
}

@Composable
fun LessonScreen(
    letter: Letter,
    stage: Stage,
    showShapes: Boolean,
    onBack: () -> Unit,
    onHear: () -> Unit,
    onNext: (() -> Unit)?,
) {
    val colors = stage.colors()
    CappedWidth(HuroofiTokens.Sky) {
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            PathHeader(PathStep.MEET, "Back to home", onBack)
            // The letter box takes whatever height the window has spare, so a tall phone shows a bigger card
            // instead of an empty gap; a short window keeps the base size and scrolls.
            BoxWithConstraints(Modifier.weight(1f)) {
                val viewport = constraints.maxHeight
                var naturalPx by remember { mutableIntStateOf(0) }
                val extraPx = if (naturalPx == 0) 0 else (viewport - naturalPx).coerceAtLeast(0)
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .onSizeChanged { naturalPx = it.height - extraPx }
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    val outer = RoundedCornerShape(LessonSpec.OuterCorner)
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .dropEdge(LessonSpec.EdgeColor, 8.dp, outer)
                            .background(colors.border, outer)
                            .padding(LessonSpec.Ring),
                    ) {
                        Column(
                            Modifier.fillMaxWidth().background(HuroofiTokens.Card, RoundedCornerShape(LessonSpec.InnerCorner)).padding(14.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.size(LessonSpec.IndexCircle).background(colors.pastel, CircleShape), contentAlignment = Alignment.Center) {
                                    Text("${letter.index}", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
                                }
                                Text(letter.nameLatin, Modifier.weight(1f), style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Navy)
                                Box(Modifier.background(colors.pastel, RoundedCornerShape(999.dp)).padding(horizontal = 16.dp)) {
                                    ArabicText(letter.nameAr, size = LessonSpec.NAME_SP.sp, color = HuroofiTokens.Navy)
                                }
                            }
                            // Naskh line metrics are much taller than the glyph; a fixed box keeps the card compact.
                            val baseBox = if (showShapes) LessonSpec.LetterBoxWithShapes else LessonSpec.LetterBox
                            val box = baseBox + with(LocalDensity.current) { extraPx.toDp() }
                            val letterWiggle = rememberWiggle()
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(box)
                                    // No clip and no ripple: the glyph overflows its box, and the wiggle is the answer.
                                    .clickable(interactionSource = null, indication = null, role = Role.Button, onClickLabel = "Hear it again") {
                                        letterWiggle.play()
                                        onHear()
                                    }
                                    .wiggle(letterWiggle),
                                contentAlignment = Alignment.Center,
                            ) {
                                val growth = (box / baseBox).coerceAtMost(LessonSpec.MAX_LETTER_GROWTH)
                                val letterSp = growth * if (showShapes) LessonSpec.LETTER_WITH_SHAPES_SP else LessonSpec.LETTER_SP
                                IgnoreFontScale {
                                    ArabicText(letter.letter, Modifier.wrapContentHeight(unbounded = true), size = letterSp.sp, color = HuroofiTokens.Navy)
                                }
                            }
                            val pictureWiggle = rememberWiggle()
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(colors.pastel)
                                    .clickable(role = Role.Button, onClickLabel = "Hear it again") {
                                        pictureWiggle.play()
                                        onHear()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Image(letterPicture(letter), contentDescription = letter.meaningEn, modifier = Modifier.size(LessonSpec.Picture).wiggle(pictureWiggle))
                                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    ArabicText(highlightedWord(letter.wordFirst, letter.wordRest, colors.accent), size = LessonSpec.WORD_SP.sp, color = HuroofiTokens.Navy)
                                    Text(letter.meaningEn, style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold), color = HuroofiTokens.Muted)
                                }
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        RoundIconButton(
                            contentDescription = "Hear the letter",
                            onClick = onHear,
                            toddler = true,
                            size = LessonSpec.Sound,
                            containerColor = HuroofiTokens.Sun,
                            shadowColor = HuroofiTokens.SunShadow,
                        ) { SoundIcon(HuroofiTokens.Navy) }
                        Column {
                            Text("Say it with me!", style = HuroofiText.buttonPrimary, color = HuroofiTokens.Navy)
                            Text("Tap to hear “${letter.nameLatin}” again", style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold), color = HuroofiTokens.Muted)
                        }
                    }
                    if (showShapes) LetterShapesGrid(letter)
                }
            }
            if (onNext != null) PrimaryButton("Next: Trace it", onClick = onNext, icon = ButtonIcons.Next)
        }
    }
}

/** Alone / Start / Middle / End, right to left; non-joining letters show two cells (decision 2). */
@Composable
private fun LetterShapesGrid(letter: Letter) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ArabicText(letter.letter, size = LessonSpec.INLINE_SP.sp, color = HuroofiTokens.Navy)
            Text("changes shape in words", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Muted)
        }
        val shapes = letterShapes(letter.letter)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for ((kind, text) in shapes) {
                    Column(
                        Modifier.weight(1f).background(HuroofiTokens.Card, RoundedCornerShape(LessonSpec.ShapeCorner)).padding(top = 6.dp, bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        IgnoreFontScale { ArabicText(text, size = LessonSpec.SHAPE_SP.sp, color = HuroofiTokens.Navy) }
                        Text(kind.label, style = HuroofiText.caption.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Muted)
                    }
                }
                // Keep cells the same width when a non-joining letter has only two.
                repeat(ShapeKind.entries.size - shapes.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 1000)
@Composable
private fun LessonReaderPreview() {
    HuroofiTheme {
        LessonScreen(LearnPreviewData.letters[1], LearnPreviewData.stage, showShapes = true, onBack = {}, onHear = {}, onNext = {})
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun LessonPreschoolPreview() {
    HuroofiTheme {
        LessonScreen(LearnPreviewData.letters[0], LearnPreviewData.stage, showShapes = false, onBack = {}, onHear = {}, onNext = {})
    }
}
