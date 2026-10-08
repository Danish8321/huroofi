package com.huroofi.app.learn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips
import com.huroofi.app.data.progress.isStageComplete
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.pickExcept
import com.huroofi.app.ui.components.LocalBuzz
import kotlin.random.Random
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Play for letter [index]: 3 right answers learn it. A new letter is saved, with its stage's
 * sticker when it completes the stage; a review letter writes nothing (decisions 1 and 5).
 * Each right answer waits for a tap on its button before the next round (plan 15 decision 2).
 */
@Composable
fun QuizRoute(index: Int, onClose: () -> Unit, onContinue: (PathNext) -> Unit) {
    val container = LocalAppContainer.current
    val content = container.content
    val progress = container.progress
    val target = remember(index) { content.letter(index) }
    val scope = rememberCoroutineScope()
    val buzz = LocalBuzz.current
    val prompt = remember { PromptPlayer(container.sound, scope) }
    val random = remember { Random(Random.nextInt()) }
    var lastPraise by remember { mutableStateOf<Int?>(null) }
    // Learned letters and option count as they were on opening; null while loading.
    var start by remember { mutableStateOf<Pair<Set<Int>, Int>?>(null) }
    var game by remember { mutableStateOf<QuizGame?>(null) }
    var next by remember { mutableStateOf<PathNext?>(null) }
    var allLearned by remember { mutableStateOf(false) }

    val question = listOf(Clips.whichStartsWith, Clips.letter(target))
    LaunchedEffect(index) {
        val done = progress.completedLetters.first()
        val count = optionCount(progress.mode.first())
        start = done to count
        game = QuizGame(target, quizRound(target, content.letters, count, random))
        prompt.play(question)
    }

    fun finish(done: Set<Int>) {
        val wasNew = index !in done
        val learned = done + index
        scope.launch {
            if (wasNew) {
                progress.markLetterComplete(index)
                val stageOf = content.letters.associate { it.index to it.stage }
                if (isStageComplete(target.stage, learned, stageOf)) progress.awardSticker(target.stage)
            }
            allLearned = learned.size == content.letters.size
            next = nextStep(content.letters, learned, index, wasNew)
        }
    }

    val current = game ?: return Loading()
    val (done, count) = start ?: return Loading()
    QuizScreen(
        game = current,
        stage = content.stage(target.stage),
        nextLabel = quizNextLabel(current, next, allLearned),
        canContinue = !current.finished || next != null,
        onClose = {
            prompt.stop()
            onClose()
        },
        onHear = { prompt.play(question) },
        onPick = { pick ->
            if (current.solved) return@QuizScreen
            val after = current.answer(pick)
            game = after
            if (!after.solved) {
                prompt.play(Clips.boing)
                return@QuizScreen
            }
            buzz.confirm()
            val n = pickExcept((1..Clips.PRAISE_COUNT).toList(), lastPraise, random)
            lastPraise = n
            prompt.play(listOf(Clips.praise(n), Clips.word(target)))
            if (after.finished) finish(done)
        },
        onContinue = {
            prompt.stop()
            if (!current.finished) {
                game = current.nextRound(quizRound(target, content.letters, count, random, avoidSlot = current.targetSlot))
                prompt.play(question)
            } else {
                next?.let(onContinue)
            }
        },
    )
}
