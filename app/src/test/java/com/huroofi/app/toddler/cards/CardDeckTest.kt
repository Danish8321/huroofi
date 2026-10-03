package com.huroofi.app.toddler.cards

import com.huroofi.app.data.content.TestContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CardDeckTest {
    private val deck = CardDeck(TestContent.repo.letters.size)

    @Test
    fun startsOnTheFirstLetter() {
        assertEquals(0, deck.indexOf(deck.startPage))
    }

    @Test
    fun wrapsBackwardsToTheLastLetter() {
        assertEquals(27, deck.indexOf(deck.startPage - 1))
    }

    @Test
    fun wrapsForwardsToTheFirstLetter() {
        assertEquals(0, deck.indexOf(deck.startPage + 28))
        assertEquals(27, deck.indexOf(deck.startPage + 27))
    }

    @Test
    fun startPageLeavesRoomBothWays() {
        assertTrue(deck.startPage > 1000)
        assertTrue(deck.pageCount - deck.startPage > 1000)
    }
}
