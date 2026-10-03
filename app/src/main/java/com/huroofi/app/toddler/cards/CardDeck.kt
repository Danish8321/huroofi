package com.huroofi.app.toddler.cards

/**
 * Maps pager pages onto letter positions so Look & listen wraps at both ends (plan 05 decision 9).
 * The pager starts in the middle of a long run of pages; [indexOf] folds any page onto 0 until [size].
 */
class CardDeck(val size: Int) {
    init {
        require(size > 0) { "deck needs at least one card" }
    }

    val pageCount: Int = size * LAPS * 2
    val startPage: Int = size * LAPS

    fun indexOf(page: Int): Int = Math.floorMod(page, size)

    private companion object {
        const val LAPS = 1000
    }
}
