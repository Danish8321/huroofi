package com.huroofi.app.toddler.find

import com.huroofi.app.data.content.Letter
import com.huroofi.app.toddler.pickExcept
import kotlin.random.Random

enum class TapResult { NUDGE, CORRECT, IGNORED }

/** One "Where's the…?" question: a target and one distractor, in random order. */
data class FindRound(
    val target: Letter,
    val options: List<Letter>,
    val solved: Boolean = false,
    val wrongTaps: Int = 0,
    val lastWrong: Letter? = null,
)

/** A set of three rounds; [stars] counts rounds solved in this set (plan 05 decision 5). */
data class FindSet(val round: FindRound, val stars: Int = 0) {
    val complete: Boolean get() = stars == FindGame.ROUNDS_PER_SET
}

/** Rules of Where's the…?: endless, no result screen, a wrong tap never costs a star. Toddler words only. */
class FindGame(letters: List<Letter>, private val random: Random) {
    private val letters = letters.filter { it.toddlerWord }

    init {
        require(letters.size >= 2) { "need at least two letters" }
    }

    fun start(): FindSet = FindSet(newRound(previousTarget = null))

    fun tap(set: FindSet, picked: Letter): Pair<FindSet, TapResult> {
        val round = set.round
        return when {
            round.solved || picked !in round.options -> set to TapResult.IGNORED
            picked == round.target ->
                set.copy(round = round.copy(solved = true), stars = set.stars + 1) to TapResult.CORRECT
            else ->
                set.copy(round = round.copy(wrongTaps = round.wrongTaps + 1, lastWrong = picked)) to TapResult.NUDGE
        }
    }

    /** Next round after a solve. After the third star a fresh set starts with no stars. */
    fun next(set: FindSet): FindSet {
        require(set.round.solved) { "next only after the round is solved" }
        val round = newRound(previousTarget = set.round.target)
        return FindSet(round, stars = if (set.complete) 0 else set.stars)
    }

    private fun newRound(previousTarget: Letter?): FindRound {
        val target = pickExcept(letters, previousTarget, random)
        val distractor = pickExcept(letters, target, random)
        return FindRound(target, listOf(target, distractor).shuffled(random))
    }

    companion object {
        const val ROUNDS_PER_SET = 3
    }
}
