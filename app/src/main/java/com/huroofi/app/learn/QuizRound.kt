package com.huroofi.app.learn

import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.progress.AgeMode
import kotlin.random.Random

/** Right answers needed to learn a letter (plan 07 decision 1). */
const val QUIZ_ROUNDS = 3

/** Pictures per round: 3 in Preschool, 4 in Early reader (decision 4). */
fun optionCount(mode: AgeMode): Int = if (mode == AgeMode.READER) 4 else 3

/**
 * The target plus distractors from all letters, shuffled. A distractor never has a word that also
 * starts with the target letter, so exactly one picture is right. The target never lands on
 * [avoidSlot], the place it held last round, so a child cannot win by tapping the same spot
 * (plan 11 decision 4).
 */
fun quizRound(target: Letter, letters: List<Letter>, optionCount: Int, random: Random, avoidSlot: Int = -1): List<Letter> {
    val distractors = letters
        .filter { it.index != target.index && it.wordFirst != target.letter && it.wordFirst != target.wordFirst }
        .shuffled(random)
        .take(optionCount - 1)
    val options = (distractors + target).shuffled(random).toMutableList()
    if (options.getOrNull(avoidSlot)?.index == target.index) {
        val swap = (options.indices - avoidSlot).random(random)
        options[avoidSlot] = options[swap].also { options[swap] = options[avoidSlot] }
    }
    return options
}

/**
 * Play: "Which one starts with …?". A wrong pick only dims that picture; a right pick counts the
 * round. There is no fail state (decision 4).
 */
data class QuizGame(
    val target: Letter,
    val options: List<Letter>,
    val roundsDone: Int = 0,
    val rounds: Int = QUIZ_ROUNDS,
    /** The last wrong picture, dimmed until the next tap. */
    val wrongPick: Letter? = null,
    /** Counts wrong taps, so tapping the same wrong picture again still replays the hint. */
    val wrongTaps: Int = 0,
    /** This round was answered; waiting for [nextRound]. */
    val solved: Boolean = false,
) {
    val finished: Boolean get() = roundsDone >= rounds

    fun isRight(pick: Letter): Boolean = pick.index == target.index

    /** Where the right picture sits this round. */
    val targetSlot: Int get() = options.indexOfFirst(::isRight)

    fun answer(pick: Letter): QuizGame = when {
        solved || finished -> this
        isRight(pick) -> copy(roundsDone = roundsDone + 1, wrongPick = null, solved = true)
        else -> copy(wrongPick = pick, wrongTaps = wrongTaps + 1)
    }

    fun nextRound(options: List<Letter>): QuizGame =
        if (!solved || finished) this else copy(options = options, wrongPick = null, solved = false)
}
