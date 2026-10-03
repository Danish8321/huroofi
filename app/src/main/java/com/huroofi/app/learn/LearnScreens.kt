package com.huroofi.app.learn

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips
import com.huroofi.app.data.progress.AgeMode
import com.huroofi.app.data.progress.unlockedStage
import com.huroofi.app.parent.parentProgress
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.ui.theme.HuroofiTokens
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Applies [learnBack] for [route]; place it inside that route's destination. */
@Composable
fun LearnBackHandler(route: String, onToGate: () -> Unit = {}, onToLesson: () -> Unit = {}, onToMap: () -> Unit = {}) {
    val action = learnBack(route)
    BackHandler(enabled = action != LearnBack.Default) {
        when (action) {
            LearnBack.ToGate -> onToGate()
            LearnBack.ToLesson -> onToLesson()
            LearnBack.ToMap -> onToMap()
            LearnBack.Default -> Unit
        }
    }
}

/** Sky while progress loads, so Home never flashes the wrong letter. */
@Composable
private fun Loading() {
    Box(Modifier.fillMaxSize().background(HuroofiTokens.Sky))
}

@Composable
fun HomeRoute(
    steps: List<PathStep>,
    navTabs: List<NavTab>,
    onStep: (PathStep, Int) -> Unit,
    onMap: (() -> Unit)?,
    onTab: (NavTab) -> Unit,
) {
    val container = LocalAppContainer.current
    val content = container.content
    val completed by container.progress.completedLetters.collectAsStateWithLifecycle<Set<Int>?>(null)
    val done = completed ?: return Loading()
    val stageOf = remember { content.letters.associate { it.index to it.stage } }
    val open = unlockedStage(done, stageOf)
    // One review pick per number of learned letters, kept across rotation.
    val seed = rememberSaveable(done.size) { Random.nextInt() }
    val today = todaysLetter(content.letters, done, open, Random(seed))
    val states = parentProgress(content.letters, content.stages, done, open).states
    HomeScreen(
        today = today,
        todayStage = content.stage(today.stage),
        review = today.index in done,
        strip = content.lettersInStage(open).sortedBy { it.index }.map { StageChip(it, states.getValue(it.index)) },
        stripStage = content.stage(open),
        steps = steps,
        onStep = { onStep(it, today.index) },
        onStrip = onMap,
        navTabs = navTabs,
        onTab = onTab,
    )
}

/** Meet: plays letter then word on open; the sound button or the picture replays both (decision 9). */
@Composable
fun LessonRoute(index: Int, onBack: () -> Unit, onNext: (() -> Unit)?) {
    val container = LocalAppContainer.current
    val letter = remember(index) { container.content.letter(index) }
    val mode by container.progress.mode.collectAsStateWithLifecycle<AgeMode?>(null)
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(container.sound, scope) }
    val clips = listOf(Clips.letter(letter), Clips.word(letter))
    LaunchedEffect(index) { prompt.play(clips) }
    val current = mode ?: return Loading()
    LessonScreen(
        letter = letter,
        stage = container.content.stage(letter.stage),
        showShapes = current == AgeMode.READER,
        onBack = {
            prompt.stop()
            onBack()
        },
        onHear = { prompt.play(clips) },
        onNext = onNext?.let { next ->
            {
                prompt.stop()
                next()
            }
        },
    )
}

/** Trace: `cheer`, a short pause, then [onDone] (decision 3). */
@Composable
fun TraceRoute(index: Int, onBack: () -> Unit, onDone: () -> Unit) {
    val container = LocalAppContainer.current
    val letter = remember(index) { container.content.letter(index) }
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(container.sound, scope) }
    TraceScreen(
        letter = letter.letter,
        onBack = {
            prompt.stop()
            onBack()
        },
        onDone = {
            scope.launch {
                val cheer = prompt.run {
                    play(Clips.cheer)
                    delay(CHEER_PAUSE_MS)
                }
                cheer.join()
                if (!cheer.isCancelled) onDone()
            }
        },
    )
}

private const val CHEER_PAUSE_MS = 400L
