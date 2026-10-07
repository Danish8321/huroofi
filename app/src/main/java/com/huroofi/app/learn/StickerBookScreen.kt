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
import com.huroofi.app.data.content.Letter
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
 * Sizes and colours of the Sticker book (plan 07 decision 5). An earned sticker answers a tap by
 * saying its stage's letters, with a wiggle (plan 11 decision 2); an empty slot does nothing.
 */
object StickerBookSpec {
    val CardCorner = 26.dp
    val CardEdge = Color(0xFFCFE2F7)
    val touchSizes = listOf(NavSpec.Item)
    val textSizes = listOf(StickerSpec.QUESTION_SP)
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, CardEdge) + NavSpec.colors
    val textPairs = listOf(ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "title")) +
        StickerSpec.textPairs + NavSpec.textPairs

    fun stagePairs(stage: StageColors) = StickerSpec.stagePairs(stage)
}

/** One slot per stage, in stage order, two per row. */
@Composable
fun StickerBookScreen(
    stages: List<Pair<Stage, List<Letter>>>,
    earned: Set<Int>,
    onSticker: (Stage) -> Unit,
    navTabs: List<NavTab>,
    onTab: (NavTab) -> Unit,
) {
    CappedWidth(HuroofiTokens.Sky) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text("Sticker book", style = HuroofiText.screenTitle, color = HuroofiTokens.Navy)
                for (row in stages.chunked(2)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        for ((stage, letters) in row) {
                            val shape = RoundedCornerShape(StickerBookSpec.CardCorner)
                            val has = stage.stage in earned
                            val wiggle = rememberWiggle()
                            Box(
                                Modifier
                                    .weight(1f)
                                    .dropEdge(StickerBookSpec.CardEdge, 6.dp, shape)
                                    .clip(shape)
                                    .background(HuroofiTokens.Card)
                                    .then(
                                        if (has) {
                                            Modifier.clickable(role = Role.Button, onClickLabel = "Hear the letters") {
                                                wiggle.play()
                                                onSticker(stage)
                                            }
                                        } else Modifier,
                                    )
                                    .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                StickerSlot(stage, letters, earned = has, modifier = Modifier.wiggle(wiggle))
                            }
                        }
                        // An odd last row keeps its slot at half width.
                        if (row.size == 1) Box(Modifier.weight(1f))
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
        stages = content.stages.sortedBy { it.stage }.map { it to content.lettersInStage(it.stage) },
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
            stages = LearnPreviewData.stages.map { it to LearnPreviewData.letters },
            earned = setOf(1, 2),
            onSticker = {},
            navTabs = NavTab.entries,
            onTab = {},
        )
    }
}
