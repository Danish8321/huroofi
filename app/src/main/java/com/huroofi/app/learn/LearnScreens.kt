package com.huroofi.app.learn

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips
import com.huroofi.app.data.progress.AgeMode
import com.huroofi.app.data.progress.unlockedStage
import com.huroofi.app.parent.parentProgress
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.ui.components.LocalBuzz
import com.huroofi.app.ui.theme.HuroofiTokens
import kotlin.random.Random

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
internal fun Loading() {
    Box(Modifier.fillMaxSize().background(HuroofiTokens.Sky))
}

@Composable
fun HomeRoute(
    navTabs: List<NavTab>,
    onGo: (Int) -> Unit,
    onPractice: (Int) -> Unit,
    onTab: (NavTab) -> Unit,
) {
    val container = LocalAppContainer.current
    val content = container.content
    val completed by container.progress.completedLetters.collectAsStateWithLifecycle<Set<Int>?>(null)
    val traceOnly by container.progress.traceOnly.collectAsStateWithLifecycle(false)
    val earned by container.progress.stickers.collectAsStateWithLifecycle<Set<Int>?>(null)
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(container.sound, scope) }
    val done = completed ?: return Loading()
    val stickers = earned ?: return Loading()
    val stageOf = remember { content.letters.associate { it.index to it.stage } }
    val open = unlockedStage(done, stageOf)
    // One review pick per number of learned letters, kept across rotation.
    val seed = rememberSaveable(done.size) { Random.nextInt() }
    val today = todaysLetter(content.letters, done, open, Random(seed))
    val states = parentProgress(content.letters, content.stages, done, open).states
    HomeScreen(
        today = today,
        stage = content.stage(today.stage),
        chips = content.lettersInStage(today.stage).sortedBy { it.index }.map { StageChip(it, states.getValue(it.index)) },
        review = today.index in done,
        traceOnly = traceOnly,
        stickerEarned = today.stage in stickers,
        onGo = {
            prompt.stop()
            if (traceOnly) onPractice(today.index) else onGo(today.index)
        },
        onHear = { prompt.play(listOf(Clips.letter(today), Clips.word(today))) },
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
        onHearLetter = { prompt.play(Clips.letter(letter)) },
        onHearWord = { prompt.play(Clips.word(letter)) },
        onNext = onNext?.let { next ->
            {
                prompt.stop()
                next()
            }
        },
    )
}

/**
 * Trace: `cheer` and praise once the letter is traced; [onNext] only when the child taps Play
 * (plan 15 decision 1).
 */
@Composable
fun TraceRoute(index: Int, onBack: () -> Unit, onNext: () -> Unit, practice: Boolean = false) {
    val container = LocalAppContainer.current
    val letter = remember(index) { container.content.letter(index) }
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(container.sound, scope) }
    val strokes = remember(index) { container.content.strokes(index) }
    // Sound first: the letter, then the spoken hint, then the demo hand starts (plan 09 decision 4, plan 14 decision 2).
    var introDone by remember(index) { mutableStateOf(false) }
    LaunchedEffect(index) {
        prompt.play(listOf(Clips.letter(letter), Clips.traceHint)).join()
        introDone = true
    }
    TraceScreen(
        letter = letter.letter,
        strokes = strokes,
        introDone = introDone,
        onHearHint = { phase ->
            prompt.play(
                when (phase) {
                    TracePhase.Trace -> Clips.traceHint
                    TracePhase.Fill -> Clips.fillHint
                    TracePhase.Done -> Clips.praise(Random.nextInt(1, Clips.PRAISE_COUNT + 1))
                },
            )
        },
        practice = practice,
        onBack = {
            prompt.stop()
            onBack()
        },
        onTraced = { prompt.play(listOf(Clips.cheer, Clips.praise(Random.nextInt(1, Clips.PRAISE_COUNT + 1)))) },
        onNext = {
            prompt.stop()
            onNext()
        },
    )
}

/**
 * Trace practice for letter [index]: no step track, Back goes Home, and "Next letter" opens the next
 * practice letter. Nothing is saved (plan 13 decision 4).
 */
@Composable
fun PracticeRoute(index: Int, onBack: () -> Unit, onNext: (Int) -> Unit) {
    val container = LocalAppContainer.current
    val completed by container.progress.completedLetters.collectAsStateWithLifecycle<Set<Int>?>(null)
    val unlockAll by container.progress.unlockAll.collectAsStateWithLifecycle(false)
    val done = completed ?: return Loading()
    TraceRoute(
        index,
        onBack = onBack,
        onNext = { onNext(nextPracticeLetter(container.content.letters, done, unlockAll, index)) },
        practice = true,
    )
}

/** Reward for [stage]: `cheer` on open (decision 5). The sticker was saved before this opened. */
@Composable
fun RewardRoute(stage: Int, onNext: () -> Unit, onStickers: () -> Unit) {
    val container = LocalAppContainer.current
    val content = container.content
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(container.sound, scope) }
    val buzz = LocalBuzz.current
    LaunchedEffect(stage) {
        buzz.confirm()
        prompt.play(Clips.cheer)
    }
    val completed by container.progress.completedLetters.collectAsStateWithLifecycle<Set<Int>?>(null)
    val done = completed ?: return Loading()
    val reward = rewardNext(stage, done, content.letters.associate { it.index to it.stage })
    RewardScreen(
        stage = content.stage(stage),
        letters = content.lettersInStage(stage),
        next = (reward as? RewardNext.Unlocked)?.let { content.stage(it.stage) },
        unlocked = reward != RewardNext.Nothing,
        onSticker = { prompt.play(content.lettersInStage(stage).sortedBy { it.index }.map(Clips::letter)) },
        onNext = {
            prompt.stop()
            onNext()
        },
        onStickers = {
            prompt.stop()
            onStickers()
        },
    )
}
