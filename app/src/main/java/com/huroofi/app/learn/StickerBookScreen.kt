package com.huroofi.app.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips
import com.huroofi.app.data.content.Stage
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors

/**
 * Sizes and colours of the Sticker book (`Stickers.html`, plan 15 decision 6). An earned sticker answers
 * a tap by saying its stage's letters, with a wiggle (plan 11 decision 2); an empty slot does nothing.
 */
object StickerBookSpec {
    val CardCorner = 28.dp
    val CardEdge = Color(0xFFCFE2F7)
    /** An empty slot is the card at 60 %, flat. */
    val EmptyCard = Color(0x99FFFFFF)
    val Gap = 14.dp
    val touchSizes = listOf(NavSpec.Item)
    val textSizes = listOf(StickerSpec.QUESTION_SP)
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, CardEdge) + NavSpec.colors
    val textPairs = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "title"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "subtitle"),
    ) + StickerSpec.textPairs + NavSpec.textPairs

    fun stagePairs(stage: StageColors) = StickerSpec.stagePairs(stage)
}

/** "2 of 7 stickers · finish a stage to earn one". */
fun stickerCount(earned: Int, total: Int): String = "$earned of $total stickers · finish a stage to earn one"

/** One slot per stage, in stage order, two per row; an odd last slot sits in the middle. */
@Composable
fun StickerBookScreen(
    stages: List<Stage>,
    earned: Set<Int>,
    onSticker: (Stage) -> Unit,
    navTabs: List<NavTab>,
    onTab: (NavTab) -> Unit,
) {
    CappedWidth(HuroofiTokens.Sky) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(StickerBookSpec.Gap),
            ) {
                Column {
                    Text("Sticker book", style = HuroofiText.screenTitle, color = HuroofiTokens.Navy)
                    Text(stickerCount(earned.size, stages.size), style = HuroofiText.body, color = HuroofiTokens.Muted)
                }
                for (row in stages.chunked(2)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(StickerBookSpec.Gap, Alignment.CenterHorizontally),
                    ) {
                        for (stage in row) {
                            val shape = RoundedCornerShape(StickerBookSpec.CardCorner)
                            val has = stage.stage in earned
                            val wiggle = rememberWiggle()
                            Box(
                                // A lone last slot keeps half the width, centred.
                                (if (row.size == 1) Modifier.fillMaxWidth(0.5f).padding(horizontal = StickerBookSpec.Gap / 4) else Modifier.weight(1f))
                                    .then(if (has) Modifier.dropEdge(StickerBookSpec.CardEdge, 5.dp, shape) else Modifier)
                                    .clip(shape)
                                    .background(if (has) HuroofiTokens.Card else StickerBookSpec.EmptyCard)
                                    .then(
                                        if (has) {
                                            Modifier.clickable(role = Role.Button, onClickLabel = "Hear the letters") {
                                                wiggle.play()
                                                onSticker(stage)
                                            }
                                        } else Modifier,
                                    )
                                    .padding(horizontal = 8.dp, vertical = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                StickerSlot(stage, earned = has, modifier = Modifier.wiggle(wiggle))
                            }
                        }
                    }
                }
            }
            LearnNav(NavTab.STICKERS, navTabs, onTab)
        }
    }
}

@Composable
fun StickerBookRoute(navTabs: List<NavTab>, onTab: (NavTab) -> Unit) {
    val container = LocalAppContainer.current
    val content = container.content
    val earned by container.progress.stickers.collectAsStateWithLifecycle<Set<Int>?>(null)
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(container.sound, scope) }
    val stickers = earned ?: return Loading()
    StickerBookScreen(
        stages = content.stages.sortedBy { it.stage },
        earned = stickers,
        onSticker = { stage -> prompt.play(content.lettersInStage(stage.stage).sortedBy { it.index }.map(Clips::letter)) },
        navTabs = navTabs,
        onTab = onTab,
    )
}

@Preview(widthDp = 390, heightDp = 1000)
@Composable
private fun StickerBookPreview() {
    HuroofiTheme {
        StickerBookScreen(
            stages = LearnPreviewData.stages,
            earned = setOf(1, 2),
            onSticker = {},
            navTabs = NavTab.entries,
            onTab = {},
        )
    }
}
