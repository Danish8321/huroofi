package com.huroofi.app.learn

import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.progress.isStageComplete
import kotlin.random.Random

/** The first letter of [openStage], in alphabet order, that is not learned; null once that stage is done. */
fun learningLetter(letters: List<Letter>, completed: Set<Int>, openStage: Int): Letter? =
    letters.sortedBy { it.index }.firstOrNull { it.stage == openStage && it.index !in completed }

/** Today's letter (glossary): the learning letter, or once all are learned a random learned letter for review. */
fun todaysLetter(letters: List<Letter>, completed: Set<Int>, openStage: Int, random: Random): Letter =
    learningLetter(letters, completed, openStage)
        ?: letters.filter { it.index in completed }.randomOrNull(random)
        ?: letters.minBy { it.index }

/** Where "Continue" goes after Play (plan 07 decision 1). */
sealed interface PathNext {
    data class Meet(val index: Int) : PathNext
    data class Reward(val stage: Int) : PathNext
    data object Home : PathNext
}

/**
 * [completed] already includes [justLearned]. [wasNew] is false for a review letter, which goes Home
 * and never repeats a Reward.
 */
fun nextStep(letters: List<Letter>, completed: Set<Int>, justLearned: Int, wasNew: Boolean): PathNext {
    if (!wasNew) return PathNext.Home
    val stageOf = letters.associate { it.index to it.stage }
    val stage = stageOf.getValue(justLearned)
    if (isStageComplete(stage, completed, stageOf)) return PathNext.Reward(stage)
    return learningLetter(letters, completed, stage)?.let { PathNext.Meet(it.index) } ?: PathNext.Home
}
