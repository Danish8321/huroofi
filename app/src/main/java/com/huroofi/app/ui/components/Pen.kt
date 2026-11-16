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
 * Picks the finger that draws (plan 16 slice 2): whichever finger is moving. A finger moves when
 * it gets [slop] away from where it last moved. A finger that stays still leaves no ink, so a
 * resting thumb or palm never blocks or smudges. A pen that has not moved for [idleMs] hands over
 * to the next finger that moves. A still finger that lifts leaves a dot (a tap), unless ink was
 * made while it was down, which is what a resting thumb looks like.
 */
class Pen(private val slop: Float, private val ink: Ink, private val idleMs: Long = 300) {
    private class Finger(var anchor: Offset, var movedAt: Long) {
        var moved = false
        var sawInk = false
    }

    private val fingers = HashMap<Long, Finger>()
    private var pen: Long? = null

    fun update(id: Long, position: Offset, pressed: Boolean, time: Long) {
        val finger = fingers[id]
        if (finger == null) {
            if (pressed) fingers[id] = Finger(position, time)
            return
        }
        if (!pressed) {
            fingers.remove(id)
            if (id == pen) {
                pen = null
                ink.end()
            } else if (pen == null && !finger.moved && !finger.sawInk) {
                draw { start(finger.anchor); end() }
            }
            return
        }
        val far = (position - finger.anchor).getDistance() > slop
        if (id == pen) {
            if (far) finger.anchor = position.also { finger.movedAt = time }
            draw { move(position) }
            return
        }
        if (!far) return
        val from = if (finger.moved) position else finger.anchor
        finger.moved = true
        finger.anchor = position
        finger.movedAt = time
        val current = pen?.let { fingers.getValue(it) }
        if (current != null && time - current.movedAt <= idleMs) return
        if (current != null) ink.end()
        pen = id
        draw { start(from); if (from != position) move(position) }
    }

    private inline fun draw(block: Ink.() -> Unit) {
        ink.block()
        fingers.values.forEach { it.sawInk = true }
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
            pen.update(down.id.value, down.position, pressed = true, down.uptimeMillis)
            do {
                val event = awaitPointerEvent()
                for (change in event.changes) {
                    change.consume()
                    pen.update(change.id.value, change.position, change.pressed, change.uptimeMillis)
                }
            } while (event.changes.any { it.pressed })
        } finally {
            pen.close()
        }
    }
}
