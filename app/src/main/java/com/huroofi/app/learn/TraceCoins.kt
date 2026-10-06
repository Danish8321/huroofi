package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

private const val RELAX_STEPS = 60

/**
 * Where each stroke's numbered coin sits, given each stroke's dots in canvas pixels. A line stroke's
 * coin sits on its first dot, where the finger starts. A dot stroke's coin sits beside its dot,
 * pushed away from the letter's [centre], so the dot to tap stays in sight (kid review, 2026-10-07).
 * Dot coins then step apart until no coin overlaps another coin or any dot, and stay inside [bounds].
 */
fun coinSpots(strokes: List<List<Offset>>, centre: Offset, coinRadius: Float, dotRadius: Float, bounds: Rect): List<Offset> {
    val isDot = strokes.map { it.size == 1 }
    val dots = strokes.filter { it.size == 1 }.map { it[0] }
    val gap = dotRadius
    val spots = strokes.mapIndexed { i, s ->
        if (!isDot[i]) s[0] else s[0] + away(s[0] - centre) * (coinRadius + dotRadius + gap)
    }.toMutableList()
    val inner = Rect(bounds.left + coinRadius, bounds.top + coinRadius, bounds.right - coinRadius, bounds.bottom - coinRadius)
    repeat(RELAX_STEPS) {
        for (i in spots.indices) {
            if (!isDot[i]) continue
            for (j in spots.indices) {
                if (j == i) continue
                // A line coin marks where to start, so only the dot coin gives way to it.
                val share = if (isDot[j]) 0.5f else 1f
                spots[i] = pushOff(spots[i], spots[j], 2 * coinRadius + gap, share)
            }
            for (d in dots) spots[i] = pushOff(spots[i], d, coinRadius + dotRadius + gap, 1f)
            spots[i] = Offset(spots[i].x.coerceIn(inner.left, inner.right), spots[i].y.coerceIn(inner.top, inner.bottom))
        }
    }
    return spots
}

/** [delta] as a unit vector; straight up when it has no length. */
private fun away(delta: Offset): Offset {
    val length = delta.getDistance()
    return if (length == 0f) Offset(0f, -1f) else delta / length
}

/** [spot] moved [share] of the way out of a [reach]-wide circle around [from]; unchanged when already clear. */
private fun pushOff(spot: Offset, from: Offset, reach: Float, share: Float): Offset {
    val delta = spot - from
    val distance = delta.getDistance()
    if (distance >= reach) return spot
    return spot + away(delta) * ((reach - distance) * share)
}
