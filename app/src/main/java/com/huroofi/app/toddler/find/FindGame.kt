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

/** Rules of Where's the…?: endless rounds, no score, no result screen (plan 15 decision 4). Toddler words only. */
class FindGame(letters: List<Letter>, private val random: Random) {
    private val letters = letters.filter { it.toddlerWord }

    init {
        require(letters.size >= 2) { "need at least two letters" }
    }

    fun start(): FindRound = newRound(previousTarget = null)

    fun tap(round: FindRound, picked: Letter): Pair<FindRound, TapResult> = when {
        round.solved || picked !in round.options -> round to TapResult.IGNORED
        picked == round.target -> round.copy(solved = true) to TapResult.CORRECT
        else -> round.copy(wrongTaps = round.wrongTaps + 1, lastWrong = picked) to TapResult.NUDGE
    }

    /** The round after a solve. */
    fun next(round: FindRound): FindRound {
        require(round.solved) { "next only after the round is solved" }
        return newRound(previousTarget = round.target)
    }

    private fun newRound(previousTarget: Letter?): FindRound {
        val target = pickExcept(letters, previousTarget, random)
        val distractor = pickExcept(letters, target, random)
        return FindRound(target, listOf(target, distractor).shuffled(random))
    }
}
