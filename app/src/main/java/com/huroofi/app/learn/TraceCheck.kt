package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset

/** Share of the glyph's sample points that must be inked (plan 07 decision 3). */
const val TRACE_NEED = 0.7f

/**
 * Forgiving trace check: a target point counts once any ink lands within [tolerancePx] of it.
 * Order, direction and ink outside the letter don't matter. Targets are bucketed in a grid of
 * [tolerancePx] cells, so each ink point only looks at its neighbourhood.
 */
class TraceCheck(private val targets: List<Offset>, private val tolerancePx: Float, private val need: Float = TRACE_NEED) {
    private val covered = BooleanArray(targets.size)
    private var count = 0
    private val buckets: Map<Pair<Int, Int>, List<Int>> = targets.indices.groupBy { cell(targets[it]) }

    val coverage: Float get() = if (targets.isEmpty()) 0f else count.toFloat() / targets.size
    val done: Boolean get() = targets.isNotEmpty() && coverage >= need

    fun addInk(point: Offset) {
        val (cx, cy) = cell(point)
        val limit = tolerancePx * tolerancePx
        for (dx in -1..1) for (dy in -1..1) {
            for (i in buckets[cx + dx to cy + dy].orEmpty()) {
                if (covered[i]) continue
                val d = targets[i] - point
                if (d.x * d.x + d.y * d.y <= limit) {
                    covered[i] = true
                    count++
                }
            }
        }
    }

    fun clear() {
        covered.fill(false)
        count = 0
    }

    private fun cell(p: Offset): Pair<Int, Int> = Math.floorDiv(p.x.toInt(), cellSize()) to Math.floorDiv(p.y.toInt(), cellSize())

    private fun cellSize(): Int = tolerancePx.toInt().coerceAtLeast(1)
}

/** Grid points, [step] apart, where [isInk] says the glyph is drawn. */
fun sampleMask(width: Int, height: Int, step: Int, isInk: (x: Int, y: Int) -> Boolean): List<Offset> {
    require(step > 0) { "step must be positive" }
    val points = mutableListOf<Offset>()
    var y = step / 2
    while (y < height) {
        var x = step / 2
        while (x < width) {
            if (isInk(x, y)) points += Offset(x.toFloat(), y.toFloat())
            x += step
        }
        y += step
    }
    return points
}
