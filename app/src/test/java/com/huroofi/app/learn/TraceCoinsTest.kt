package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.huroofi.app.data.content.ContentJson
import com.huroofi.app.data.content.LetterStrokes
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Coins never hide a dot or each other, on every letter (kid review, 2026-10-07). Sizes are the phone's at density 1. */
class TraceCoinsTest {
    private val letters: List<LetterStrokes> by lazy {
        ContentJson.decodeStrokes(File("../data/strokes.json").readText(Charsets.UTF_8)).letters
    }

    private fun spots(l: LetterStrokes): Pair<List<List<Offset>>, List<Offset>> {
        val strokes = l.strokes.sortedBy { it.order }.map { s -> strokeDots(s.points.map { Offset(MARGIN + it[0] * BOX, MARGIN + it[1] * BOX) }, SPACING) }
        val centre = Offset(MARGIN + BOX / 2, MARGIN + BOX / 2)
        return strokes to coinSpots(strokes, centre, COIN, DOT, Rect(0f, 0f, BOX + 2 * MARGIN, BOX + 2 * MARGIN))
    }

    @Test
    fun aLineCoinSitsWhereTheStrokeStarts() {
        letters.forEach { l ->
            val (strokes, coins) = spots(l)
            strokes.indices.filter { strokes[it].size > 1 }.forEach { assertEquals("letter ${l.index}", strokes[it][0], coins[it]) }
        }
    }

    @Test
    fun noDotCoinCoversADot() {
        letters.forEach { l ->
            val (strokes, coins) = spots(l)
            val dots = strokes.filter { it.size == 1 }.map { it[0] }
            strokes.indices.filter { strokes[it].size == 1 }.forEach { i ->
                dots.forEach { d -> assertTrue("letter ${l.index} coin $i", (coins[i] - d).getDistance() >= COIN + DOT - 0.5f) }
            }
        }
    }

    @Test
    fun noDotCoinOverlapsAnotherCoin() {
        letters.forEach { l ->
            val (strokes, coins) = spots(l)
            strokes.indices.filter { strokes[it].size == 1 }.forEach { i ->
                coins.indices.filter { it != i }.forEach { j ->
                    assertTrue("letter ${l.index} coins $i,$j", (coins[i] - coins[j]).getDistance() >= 2 * COIN - 0.5f)
                }
            }
        }
    }

    @Test
    fun aDotCoinStaysNearItsDot() {
        letters.forEach { l ->
            val (strokes, coins) = spots(l)
            strokes.indices.filter { strokes[it].size == 1 }.forEach { i ->
                assertTrue("letter ${l.index} coin $i", (coins[i] - strokes[i][0]).getDistance() <= 4 * COIN)
            }
        }
    }

    private companion object {
        const val BOX = 280f
        const val MARGIN = 35f
        const val COIN = 19f // TraceSpec.CoinRadius
        const val DOT = 9f // TraceSpec.DotStrokeRadius
        const val SPACING = 18f
    }
}
