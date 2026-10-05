package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import kotlin.math.ceil

/** Share of each stroke's dots that must be inked (plan 09 decision 3). */
const val STROKE_NEED = 0.7f

/**
 * [points] resampled evenly, about [spacingPx] apart, first and last point included.
 * A single point (a dot stroke) gives itself.
 */
fun strokeDots(points: List<Offset>, spacingPx: Float): List<Offset> {
    require(spacingPx > 0f) { "spacing must be positive" }
    if (points.size <= 1) return points
    val lengths = points.zipWithNext { a, b -> (b - a).getDistance() }
    val total = lengths.sum()
    if (total == 0f) return listOf(points.first())
    val segments = ceil(total / spacingPx).toInt().coerceAtLeast(1)
    val dots = mutableListOf(points.first())
    var edge = 0
    var edgeStart = 0f
    for (k in 1 until segments) {
        val at = total * k / segments
        while (edge < lengths.lastIndex && edgeStart + lengths[edge] < at) edgeStart += lengths[edge++]
        val t = if (lengths[edge] == 0f) 0f else (at - edgeStart) / lengths[edge]
        dots += points[edge] + (points[edge + 1] - points[edge]) * t
    }
    dots += points.last()
    return dots
}

/**
 * Forgiving stroke check: a dot counts once any ink lands within [tolerancePx] of it. A stroke is
 * finished at [need] coverage; the trace is done when every stroke is. Order, direction and ink
 * outside the letter never matter. [strokes] holds each stroke's dots, in writing order.
 */
class StrokeCheck(private val strokes: List<List<Offset>>, private val tolerancePx: Float, private val need: Float = STROKE_NEED) {
    private val hit = strokes.map { BooleanArray(it.size) }

    fun addInk(point: Offset) {
        val limit = tolerancePx * tolerancePx
        for (s in strokes.indices) for (d in strokes[s].indices) {
            if (hit[s][d]) continue
            val delta = strokes[s][d] - point
            if (delta.x * delta.x + delta.y * delta.y <= limit) hit[s][d] = true
        }
    }

    fun covered(stroke: Int, dot: Int): Boolean = hit[stroke][dot]

    fun coverage(stroke: Int): Float = if (hit[stroke].isEmpty()) 0f else hit[stroke].count { it }.toFloat() / hit[stroke].size

    fun finished(stroke: Int): Boolean = hit[stroke].isNotEmpty() && coverage(stroke) >= need

    val done: Boolean get() = strokes.isNotEmpty() && strokes.indices.all(::finished)

    /** The lowest-numbered stroke not yet finished, or null when all are. */
    fun nextStroke(): Int? = strokes.indices.firstOrNull { !finished(it) }

    fun clear() {
        hit.forEach { it.fill(false) }
    }
}
