package com.huroofi.app.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope

/** What a drawing surface does with the pen: one stroke at a time. */
interface Ink {
    fun start(at: Offset)
    fun move(to: Offset)
    fun end()
}

/**
 * Picks the finger that draws (plan 16 slice 2). A finger becomes the pen once it moves past
 * [slop]; a finger that stays still leaves no ink, so a resting thumb or palm never blocks or
 * smudges. A lone finger that lifts without moving is a tap and leaves a dot. While the pen is
 * down, other fingers are ignored; once it lifts, the next finger to move takes over.
 */
class Pen(private val slop: Float, private val ink: Ink) {
    private class Finger(val anchor: Offset) {
        var moved = false
        var alone = true
    }

    private val fingers = HashMap<Long, Finger>()
    private var pen: Long? = null

    fun update(id: Long, position: Offset, pressed: Boolean) {
        val finger = fingers[id]
        when {
            finger == null && pressed -> {
                fingers[id] = Finger(position)
                if (fingers.size > 1) fingers.values.forEach { it.alone = false }
            }
            finger == null -> Unit
            !pressed -> {
                fingers.remove(id)
                if (id == pen) {
                    pen = null
                    ink.end()
                } else if (pen == null && !finger.moved && finger.alone) {
                    ink.start(finger.anchor)
                    ink.end()
                }
            }
            id == pen -> ink.move(position)
            !finger.moved && (position - finger.anchor).getDistance() <= slop -> Unit
            else -> {
                val first = !finger.moved
                finger.moved = true
                if (pen == null) {
                    pen = id
                    ink.start(if (first) finger.anchor else position)
                    if (first) ink.move(position)
                }
            }
        }
    }

    /** The gesture stopped early (cancelled): close the open stroke. */
    fun close() {
        if (pen != null) ink.end()
        pen = null
        fingers.clear()
    }
}

/** Runs [Pen] over every gesture on this surface. [onTouch] fires on each first finger down. */
suspend fun PointerInputScope.drawWithPen(ink: Ink, onTouch: () -> Unit = {}) {
    awaitEachGesture {
        val pen = Pen(viewConfiguration.touchSlop, ink)
        try {
            val down = awaitFirstDown()
            down.consume()
            onTouch()
            pen.update(down.id.value, down.position, pressed = true)
            do {
                val event = awaitPointerEvent()
                for (change in event.changes) {
                    change.consume()
                    pen.update(change.id.value, change.position, change.pressed)
                }
            } while (event.changes.any { it.pressed })
        } finally {
            pen.close()
        }
    }
}
