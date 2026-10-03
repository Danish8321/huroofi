package com.huroofi.app.parent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.data.content.Stage
import com.huroofi.app.data.progress.AgeMode
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.NotoNaskhArabic

/** Sizes and colours of the Parent zone, from `Parents.html` (plan 06 decision 9). */
object ZoneSpec {
    val PagePadding = 20.dp
    val Gap = 16.dp
    val CardRadius = 22.dp
    val CardPadding = 16.dp
    val CardBorder = Color(0xFFD5E2F0)
    val BackButton = 48.dp
    val Avatar = 52.dp
    val AvatarFill = Color(0xFFFFF4D6)
    val BarHeight = 14.dp
    val BarTrack = Color(0xFFE3ECF7)
    val Legend = 12.dp
    val Learned = HuroofiTokens.Success
    val Learning = HuroofiTokens.Primary
    val ToGoLegend = Color(0xFFC9D6E6)
    val Cell = 42.dp
    val CellGap = 6.dp
    val CellRadius = 12.dp
    val CellRing = 3.dp
    val CellGlyph = 26.sp
    val ToGoCell = Color(0xFFEEF3F9)
    const val GRID_COLUMNS = 7

    val touchSizes = listOf(BackButton)

    /** Text colour on background, for the contrast sweep. */
    val textPairs = listOf(
        HuroofiTokens.Navy to HuroofiTokens.ParentBg,
        HuroofiTokens.Navy to Color.White,
        HuroofiTokens.Muted to Color.White,
        Color.White to Learned,
        HuroofiTokens.Muted to ToGoCell,
    )
}

/** One cell of the letter grid. */
data class GridLetter(val glyph: String, val name: String, val state: LetterState)

@Composable
fun ParentZoneScreen(
    progress: ParentProgress,
    letters: List<GridLetter>,
    mode: AgeMode,
    onBack: () -> Unit,
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalHuroofiColors.current.parentBg)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(ZoneSpec.PagePadding),
        verticalArrangement = Arrangement.spacedBy(ZoneSpec.Gap),
    ) {
        ZoneHeader(onBack)
        ChildCard(progress, mode)
        ZoneCard {
            Text("All ${letters.size} letters", style = HuroofiText.sectionHeading, color = HuroofiTokens.Navy)
            LetterGrid(letters)
        }
        footer()
    }
}

@Composable
private fun ZoneHeader(onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier
                .size(ZoneSpec.BackButton)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, ZoneSpec.CardBorder, CircleShape)
                .semantics { contentDescription = "Back to kid mode" }
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) { ChevronIcon(HuroofiTokens.Navy, pointsRight = false, size = 24.dp) }
        Text(
            "Parent zone",
            style = HuroofiText.screenTitle.copy(fontSize = 26.sp),
            color = HuroofiTokens.Navy,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Composable
fun ZoneCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(ZoneSpec.CardRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape)
            .border(1.dp, ZoneSpec.CardBorder, shape)
            .padding(ZoneSpec.CardPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun ChildCard(progress: ParentProgress, mode: AgeMode) {
    ZoneCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painterResource(R.drawable.pic_lion),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ZoneSpec.Avatar)
                    .clip(CircleShape)
                    .background(ZoneSpec.AvatarFill)
                    .border(2.dp, HuroofiTokens.Sun, CircleShape)
                    .scale(1.15f),
            )
            Column {
                Text("Your child", style = HuroofiText.sectionHeading, color = HuroofiTokens.Navy)
                Text(
                    "Stage ${progress.stage.stage} of ${progress.stageCount} · ${progress.stage.name}",
                    style = HuroofiText.caption,
                    color = HuroofiTokens.Muted,
                )
            }
        }
        ProgressBar(progress)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendItem(ZoneSpec.Learned, "${progress.learned} learned")
            LegendItem(ZoneSpec.Learning, "${progress.learning} learning")
            LegendItem(ZoneSpec.ToGoLegend, "${progress.toGo} to go")
        }
        if (mode == AgeMode.TODDLER) {
            Text(
                "Toddler play isn't tracked. Letters count once lessons start (Preschool mode).",
                style = HuroofiText.caption,
                color = HuroofiTokens.Muted,
            )
        }
    }
}

@Composable
private fun ProgressBar(progress: ParentProgress) {
    val total = progress.states.size.coerceAtLeast(1).toFloat()
    Row(
        Modifier
            .fillMaxWidth()
            .height(ZoneSpec.BarHeight)
            .clip(CircleShape)
            .background(ZoneSpec.BarTrack),
    ) {
        val learned = progress.learned / total
        val learning = progress.learning / total
        if (learned > 0f) Box(Modifier.fillMaxHeight().weight(learned).background(ZoneSpec.Learned))
        if (learning > 0f) Box(Modifier.fillMaxHeight().weight(learning).background(ZoneSpec.Learning))
        val rest = 1f - learned - learning
        if (rest > 0f) Spacer(Modifier.weight(rest))
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(ZoneSpec.Legend).background(color, RoundedCornerShape(4.dp)))
        Text(label, style = HuroofiText.caption, color = HuroofiTokens.Muted)
    }
}

/** 7 columns, alif top-right (plan 06 decision 3: display only). */
@Composable
private fun LetterGrid(letters: List<GridLetter>) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(verticalArrangement = Arrangement.spacedBy(ZoneSpec.CellGap)) {
            for (row in letters.chunked(ZoneSpec.GRID_COLUMNS)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ZoneSpec.CellGap)) {
                    for (letter in row) LetterCell(letter, Modifier.weight(1f))
                    repeat(ZoneSpec.GRID_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun LetterCell(letter: GridLetter, modifier: Modifier) {
    val shape = RoundedCornerShape(ZoneSpec.CellRadius)
    val (fill, glyph) = when (letter.state) {
        LetterState.LEARNED -> ZoneSpec.Learned to Color.White
        LetterState.LEARNING -> Color.White to HuroofiTokens.Navy
        LetterState.TO_GO -> ZoneSpec.ToGoCell to HuroofiTokens.Muted
    }
    val status = when (letter.state) {
        LetterState.LEARNED -> "learned"
        LetterState.LEARNING -> "learning"
        LetterState.TO_GO -> "to go"
    }
    Box(
        modifier
            .height(ZoneSpec.Cell)
            .background(fill, shape)
            .then(
                if (letter.state == LetterState.LEARNING) Modifier.border(ZoneSpec.CellRing, ZoneSpec.Learning, shape)
                else Modifier,
            )
            .semantics(mergeDescendants = true) { contentDescription = "${letter.name}, $status" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            letter.glyph,
            color = glyph,
            style = HuroofiText.arabicChip.copy(fontFamily = NotoNaskhArabic, fontSize = ZoneSpec.CellGlyph),
        )
    }
}

@Preview(widthDp = 390, heightDp = 1000)
@Composable
private fun ParentZonePreview() {
    val stage = Stage(1, "Sunny Meadow", emptyList(), "#FFC93C", "#FFF4D6", "#8A5A00")
    val glyphs = listOf("أ", "ب", "ت", "ث", "ج", "ح", "خ")
    val letters = List(28) { i ->
        val state = when (i) {
            0, 1 -> LetterState.LEARNED
            2 -> LetterState.LEARNING
            else -> LetterState.TO_GO
        }
        GridLetter(glyphs[i % glyphs.size], "letter ${i + 1}", state)
    }
    val progress = ParentProgress(stage, 7, letters.mapIndexed { i, l -> i + 1 to l.state }.toMap())
    HuroofiTheme { ParentZoneScreen(progress, letters, AgeMode.TODDLER, onBack = {}) }
}
