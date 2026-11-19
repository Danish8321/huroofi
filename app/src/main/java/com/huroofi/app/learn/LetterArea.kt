package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset

/** Distance from [p] to the segment [a]–[b]; a zero-length segment is the point [a]. */
fun segmentDistance(p: Offset, a: Offset, b: Offset): Float {
    val ab = b - a
    val len2 = ab.x * ab.x + ab.y * ab.y
    if (len2 == 0f) return (p - a).getDistance()
    val t = (((p.x - a.x) * ab.x + (p.y - a.y) * ab.y) / len2).coerceIn(0f, 1f)
    return (p - (a + ab * t)).getDistance()
}

/** Distance from [p] to the nearest point of [line]; a one-point line is that point. */
private fun lineDistance(p: Offset, line: List<Offset>): Float =
    if (line.size == 1) (p - line[0]).getDistance()
    else line.zipWithNext { a, b -> segmentDistance(p, a, b) }.min()

/**
 * The brush radius that paints all of [area] when a finger runs down the middle of every line in
 * [lines]: the furthest area point from any line, plus [slackPx] for a wobbly finger, and never
 * under [minPx] (plan 15 decision 1).
 */
fun brushReach(area: List<Offset>, lines: List<List<Offset>>, slackPx: Float, minPx: Float): Float {
    val strokes = lines.filter { it.isNotEmpty() }
    if (area.isEmpty() || strokes.isEmpty()) return minPx
    val furthest = area.maxOf { p -> strokes.minOf { lineDistance(p, it) } }
    return maxOf(minPx, furthest + slackPx)
}

/** Share of the letter's spots to paint: all of them (plan 15 decision 1). */
const val AREA_NEED = 1f

/** Preschool's share; the gaps take the paint once the letter is done (plan 16 slice 17). */
const val PRESCHOOL_AREA_NEED = 0.9f

/**
 * Which spots of the letter are painted (plan 15 decision 1). [points] is a grid inside the letter;
 * ink paints every point within [reachPx] of its path. Painted spots stay painted until [clear].
 * Done at [need] of the spots.
 */
class AreaCheck(val points: List<Offset>, val reachPx: Float, private val need: Float = AREA_NEED) {
    private val painted = BooleanArray(points.size)

    /** Paints along the ink from [from] to [to]; pass the same point twice for a tap. */
    fun paint(from: Offset, to: Offset) {
        for (k in points.indices) {
            if (!painted[k] && segmentDistance(points[k], from, to) <= reachPx) painted[k] = true
        }
    }

    fun painted(k: Int): Boolean = painted[k]

    val done: Boolean get() = painted.isEmpty() || painted.count { it }.toFloat() / painted.size >= need

    fun clear() {
        painted.fill(false)
    }
}
