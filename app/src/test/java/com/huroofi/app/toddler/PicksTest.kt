package com.huroofi.app.toddler

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PicksTest {
    @Test
    fun neverReturnsTheExcludedItem() {
        val random = Random(7)
        val items = listOf(1, 2, 3)
        repeat(1000) { assertNotEquals(2, pickExcept(items, 2, random)) }
    }

    @Test
    fun reachesEveryOtherItem() {
        val random = Random(7)
        val seen = (1..200).map { pickExcept(listOf(1, 2, 3), 1, random) }.toSet()
        assertEquals(setOf(2, 3), seen)
    }

    @Test
    fun singleItemIsReturnedEvenIfExcluded() {
        assertEquals(5, pickExcept(listOf(5), 5, Random(1)))
    }
}
