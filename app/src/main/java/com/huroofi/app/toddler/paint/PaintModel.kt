package com.huroofi.app.toddler.paint

import androidx.compose.ui.graphics.Color
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.TraceStroke
import com.huroofi.app.toddler.pickExcept
import kotlin.math.hypot
import kotlin.random.Random

/** The four paints. Coral is not a feedback colour, so it is allowed (plan 05 decision 2). */
enum class Crayon(val color: Color, val label: String) {
    CORAL(Color(0xFFFF7A59), "Orange paint"),
    BLUE(Color(0xFF3B8CF0), "Blue paint"),
    GREEN(Color(0xFF3BAA5C), "Green paint"),
    PURPLE(Color(0xFF8A6CE8), "Purple paint"),
}

/** A point on the canvas, in dp from its top-left corner. */
data class PaintPoint(val x: Float, val y: Float)

data class PaintStroke(val crayon: Crayon, val points: List<PaintPoint>)

/**
 * What is on the canvas. The letter is done by distance painted, not by pointer events (plan 05
 * decision 11), and only paint on the letter counts, since paint off it doesn't show (plan 12
 * decision 4).
 */
data class Painting(
    val crayon: Crayon = Crayon.CORAL,
    val strokes: List<PaintStroke> = emptyList(),
    val totalDp: Float = 0f,
) {
    val done: Boolean get() = totalDp >= GOAL_DP

    fun select(crayon: Crayon): Painting = copy(crayon = crayon)

    fun start(p: PaintPoint): Painting = copy(strokes = strokes + PaintStroke(crayon, listOf(p)))

    /** Extends the last stroke to [p]; the move counts towards the goal only when [onLetter]. */
    fun moveTo(p: PaintPoint, onLetter: Boolean = true): Painting {
        val last = strokes.lastOrNull() ?: return start(p)
        val from = last.points.last()
        val moved = last.copy(points = last.points + p)
        return copy(strokes = strokes.dropLast(1) + moved, totalDp = if (onLetter) totalDp + hypot(p.x - from.x, p.y - from.y) else totalDp)
    }

    /** Clears the canvas and the distance, also for "Paint it again". The chosen crayon stays. */
    fun wipe(): Painting = Painting(crayon = crayon)

    companion object {
        const val GOAL_DP = 400f
    }
}

data class PaintPage(val letter: Letter, val painting: Painting)

/** Picks letters to paint: any of the 28, never the same one twice in a row (decisions 3, 4, 10). */
class PaintSession(private val letters: List<Letter>, private val random: Random) {
    fun start(): PaintPage = PaintPage(pickExcept(letters, null, random), Painting())

    /** A new letter and a clean canvas; the crayon stays. */
    fun next(page: PaintPage): PaintPage =
        PaintPage(pickExcept(letters, page.letter, random), page.painting.wipe())
}

/** The strokes the demo hand travels in Finger paint: the lines, in writing order. A dot has nothing to travel. */
fun demoStrokes(strokes: List<TraceStroke>): List<Int> = strokes.indices.filter { strokes[it].points.size >= 2 }
