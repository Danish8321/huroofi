package com.huroofi.app.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.parent.LetterState
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.IgnoreFontScale
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.highlightedWord

/** Sizes and colours of Home (`Main.html`, plan 07 decision 7). */
object HomeSpec {
    val Avatar = 58.dp
    val Gap = 22.dp
    val CardCorner = 30.dp
    val CardShadow = Color(0xFFCFE2F7)
    val Edge = 8.dp
    val Picture = 128.dp
    val LetterBox = 150.dp
    val TileHeight = 80.dp
    val TileCorner = 24.dp
    val TileIconBox = 48.dp
    val Chip = 40.dp
    val StripMinHeight = 64.dp
    const val LETTER_SP = 112f
    const val WORD_SP = 32f
    const val CHIP_SP = 28f
    const val GREETING_SP = 26f
    val textSizes = listOf(LETTER_SP, WORD_SP, CHIP_SP, GREETING_SP)

    val ChipLearned = HuroofiTokens.Success
    val ChipLearningRing = HuroofiTokens.Primary

    /** Tile face, bottom edge, icon colour, as in the prototype. */
    val tileColors = mapOf(
        PathStep.MEET to Triple(Color(0xFFCFE4FF), Color(0xFFA9CBF2), HuroofiTokens.Primary),
        PathStep.TRACE to Triple(Color(0xFFFFD0E2), Color(0xFFF2A9C6), Color(0xFFD6246E)),
        PathStep.PLAY to Triple(Color(0xFFD6F2C8), Color(0xFFB2DDA0), HuroofiTokens.Success),
    )

    val touchSizes = listOf(TileHeight, StripMinHeight, NavSpec.Item)
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, CardShadow) +
        tileColors.values.flatMap { it.toList() } + NavSpec.colors

    /** Letter chips, shared with the Map. Arabic is bold 28 sp, so large. */
    val chipPairs = listOf(
        ContrastPair(HuroofiTokens.Card, ChipLearned, large = true, "learned chip letter"),
        ContrastPair(ChipLearned, HuroofiTokens.Card, large = true, "learned chip fill on the strip"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "learning chip letter"),
        ContrastPair(ChipLearningRing, HuroofiTokens.Card, large = true, "learning chip ring"),
    )

    fun chipStagePairs(stage: StageColors) =
        listOf(ContrastPair(stage.accent, stage.pastel, large = true, "to-go chip letter"))

    /** 18 sp ExtraBold is under the 18.66 sp bold line, so normal text. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "greeting"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "greeting subtitle"),
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = false, "TODAY'S LETTER"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "today's letter"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "letter name"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "word"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "stage strip title"),
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = false, "strip Map link"),
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = true, "strip chevron"),
    ) + tileColors.flatMap { (step, c) ->
        listOf(
            ContrastPair(c.third, HuroofiTokens.Card, large = true, "$step tile icon"),
            ContrastPair(HuroofiTokens.Navy, c.first, large = false, "$step tile label"),
        )
    } + ButtonKind.Primary.contrastPairs() + chipPairs + NavSpec.textPairs

    fun stagePairs(stage: StageColors) =
        listOf(ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "word first letter")) + chipStagePairs(stage)
}

/** One chip of the stage strip: the open stage's letters (decision 7). */
data class StageChip(val letter: Letter, val state: LetterState)

@Composable
fun HomeScreen(
    today: Letter,
    todayStage: Stage,
    review: Boolean,
    strip: List<StageChip>,
    stripStage: Stage,
    steps: List<PathStep>,
    onStep: (PathStep) -> Unit,
    onStrip: (() -> Unit)?,
    navTabs: List<NavTab>,
    onTab: (NavTab) -> Unit,
) {
    CappedWidth(HuroofiTokens.Sky) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            BoxWithConstraints(Modifier.weight(1f)) {
                // At least the screen tall, so spare height is shared between the gaps instead of pooling above the nav.
                Column(Modifier.verticalScroll(rememberScrollState()).heightIn(min = maxHeight).padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Greeting(review)
                    SectionGap()
                    TodayCard(today, todayStage, onGo = { onStep(PathStep.MEET) })
                    SectionGap()
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (step in steps) StepTile(step, Modifier.weight(1f)) { onStep(step) }
                    }
                    SectionGap()
                    StageStrip(strip, stripStage, onStrip)
                }
            }
            LearnNav(NavTab.HOME, navTabs, onTab)
        }
    }
}

/** [HomeSpec.Gap] plus an equal share of any spare height. */
@Composable
private fun ColumnScope.SectionGap() {
    Spacer(Modifier.height(HomeSpec.Gap))
    Spacer(Modifier.weight(1f))
}

@Composable
private fun Greeting(review: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Image(
            painterResource(R.drawable.pic_lion),
            contentDescription = null,
            modifier = Modifier
                .size(HomeSpec.Avatar)
                .clip(CircleShape)
                .background(HuroofiTokens.Card)
                .border(3.dp, HuroofiTokens.Sun, CircleShape),
        )
        Column {
            Text("Hi there!", style = HuroofiText.screenTitle.copy(fontSize = HomeSpec.GREETING_SP.sp), color = HuroofiTokens.Navy)
            Text(
                if (review) "Let's practise a letter!" else "Ready for a new letter?",
                style = HuroofiText.body,
                color = HuroofiTokens.Muted,
            )
        }
    }
}

@Composable
private fun TodayCard(letter: Letter, stage: Stage, onGo: () -> Unit) {
    val colors = stage.colors()
    val shape = RoundedCornerShape(HomeSpec.CardCorner)
    Column(
        Modifier
            .fillMaxWidth()
            .dropEdge(HomeSpec.CardShadow, HomeSpec.Edge, shape)
            .clip(shape)
            .background(HuroofiTokens.Card)
            .padding(20.dp),
    ) {
        Box(Modifier.fillMaxWidth()) {
            // Decorative bubbles in the stage pastel, top-end corner as in the prototype.
            Box(Modifier.align(Alignment.TopEnd).offset(30.dp, (-58).dp).size(120.dp).background(colors.pastel, CircleShape))
            Box(Modifier.align(Alignment.TopEnd).offset((-60).dp, (-30).dp).size(70.dp).background(colors.pastel, CircleShape))
            Column {
                Text(
                    "TODAY'S LETTER",
                    style = HuroofiText.caption.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.3.sp),
                    color = HuroofiTokens.Primary,
                )
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Box(Modifier.height(HomeSpec.LetterBox), contentAlignment = Alignment.Center) {
                            IgnoreFontScale {
                                ArabicText(letter.letter, Modifier.wrapContentHeight(unbounded = true), size = HomeSpec.LETTER_SP.sp, color = HuroofiTokens.Navy)
                            }
                        }
                        Text(letter.nameLatin, style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Navy)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(letterPicture(letter), contentDescription = letter.meaningEn, modifier = Modifier.size(HomeSpec.Picture))
                        ArabicText(
                            highlightedWord(letter.wordFirst, letter.wordRest, colors.accent),
                            size = HomeSpec.WORD_SP.sp,
                            color = HuroofiTokens.Navy,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Let's go!", onClick = onGo, icon = ButtonIcons.Next)
    }
}

@Composable
private fun StepTile(step: PathStep, modifier: Modifier, onClick: () -> Unit) {
    val (face, edge, icon) = HomeSpec.tileColors.getValue(step)
    val shape = RoundedCornerShape(HomeSpec.TileCorner)
    Column(
        modifier
            .height(HomeSpec.TileHeight)
            .dropEdge(edge, 5.dp, shape)
            .clip(shape)
            .background(face)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(HomeSpec.TileIconBox - 12.dp).background(HuroofiTokens.Card, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) { LineIcon(LearnIcons.forStep(step), icon, size = 24.dp) }
        Text(step.tile, style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
    }
}

@Composable
private fun StageStrip(chips: List<StageChip>, stage: Stage, onClick: (() -> Unit)?) {
    val shape = RoundedCornerShape(HomeSpec.TileCorner)
    Row(
        Modifier
            .fillMaxWidth()
            .dropEdge(HomeSpec.CardShadow, 5.dp, shape)
            .clip(shape)
            .background(HuroofiTokens.Card)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "Stage ${stage.stage} · ${stage.name}",
                style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold),
                color = HuroofiTokens.Navy,
            )
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (chip in chips) StageLetterChip(chip, stage)
                }
            }
        }
        if (onClick != null) {
            Spacer(Modifier.width(8.dp))
            Text("Map", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Primary)
            LineIcon(LearnIcons.Next, HuroofiTokens.Primary, size = 22.dp, strokeWidth = 2.6f)
        }
    }
}

/** Chip palette shared by Home and the Map (decisions 7, 8): learned green, learning blue ring, rest pastel. */
@Composable
fun StageLetterChip(chip: StageChip, stage: Stage, locked: Boolean = false) {
    val colors = stage.colors()
    val shape = RoundedCornerShape(12.dp)
    val (fill, text) = when {
        locked -> LockedChip to HuroofiTokens.Muted
        chip.state == LetterState.LEARNED -> HomeSpec.ChipLearned to HuroofiTokens.Card
        chip.state == LetterState.LEARNING -> HuroofiTokens.Card to HuroofiTokens.Navy
        else -> colors.pastel to colors.accent
    }
    val state = when {
        locked -> "locked"
        chip.state == LetterState.LEARNED -> "learned"
        chip.state == LetterState.LEARNING -> "learning"
        else -> "to go"
    }
    Box(
        Modifier
            .size(HomeSpec.Chip)
            .background(fill, shape)
            .then(if (!locked && chip.state == LetterState.LEARNING) Modifier.border(3.dp, HomeSpec.ChipLearningRing, shape) else Modifier)
            // One TalkBack stop per chip: the name and state, not the glyph again (plan 08 task 6.2).
            .clearAndSetSemantics { contentDescription = "${chip.letter.nameLatin}, $state" },
        contentAlignment = Alignment.Center,
    ) {
        CenteredLetter(chip.letter.letter, size = HomeSpec.CHIP_SP.sp, color = text, style = HuroofiText.arabicChip)
    }
}

/** Locked stage chips and cards (decision 8). */
val LockedChip = Color(0xFFEEF3F9)

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun HomePreview() {
    HuroofiTheme {
        val stage = LearnPreviewData.stage
        val letters = LearnPreviewData.letters
        HomeScreen(
            today = letters[1],
            todayStage = stage,
            review = false,
            strip = letters.mapIndexed { i, l ->
                StageChip(l, if (i == 0) LetterState.LEARNED else if (i == 1) LetterState.LEARNING else LetterState.TO_GO)
            },
            stripStage = stage,
            steps = PathStep.entries,
            onStep = {},
            onStrip = {},
            navTabs = NavTab.entries,
            onTab = {},
        )
    }
}
