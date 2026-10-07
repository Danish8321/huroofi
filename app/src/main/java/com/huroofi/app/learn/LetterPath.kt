package com.huroofi.app.learn

import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.data.progress.isStageComplete
import com.huroofi.app.data.progress.unlockedStage
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

/** How a stage shows on the Letter Map (decision 8). OPEN is a later stage the child may start under Unlock all (plan 13 decision 3). */
enum class StageState { FINISHED, CURRENT, OPEN, LOCKED }

/**
 * Keyed by stage number. Every complete stage is finished; the open stage, if not complete, is current;
 * the rest are locked, or open when [unlockAll] is on. Once all letters are learned there is no current stage.
 */
fun stageStates(stages: List<Stage>, completed: Set<Int>, stageOf: Map<Int, Int>, unlockAll: Boolean = false): Map<Int, StageState> {
    val open = unlockedStage(completed, stageOf)
    return stages.associate { s ->
        s.stage to when {
            isStageComplete(s.stage, completed, stageOf) -> StageState.FINISHED
            s.stage == open -> StageState.CURRENT
            unlockAll -> StageState.OPEN
            else -> StageState.LOCKED
        }
    }
}

/** What the Reward says opens next (plan 13 decision 3). */
sealed interface RewardNext {
    /** The completion moved progress on; [stage] is the new open stage. */
    data class Unlocked(val stage: Int) : RewardNext
    data object AllLearned : RewardNext
    /** A stage finished ahead of progress under Unlock all: nothing new opens. */
    data object Nothing : RewardNext
}

/**
 * [completed] already includes [stage]'s letters. Progress moved on exactly when every stage up to
 * [stage] is complete, since [stage] was then the open stage before this completion.
 */
fun rewardNext(stage: Int, completed: Set<Int>, stageOf: Map<Int, Int>): RewardNext {
    if (completed.containsAll(stageOf.keys)) return RewardNext.AllLearned
    val open = unlockedStage(completed, stageOf)
    return if (open > stage) RewardNext.Unlocked(open) else RewardNext.Nothing
}

/** A letter of a finished stage to play again: any of [indices] but [last], unless it is the only one (plan 11 decision 2). */
fun replayLetter(indices: List<Int>, last: Int, random: Random): Int {
    require(indices.isNotEmpty()) { "a stage has letters" }
    return (indices - last).ifEmpty { indices }.random(random)
}
