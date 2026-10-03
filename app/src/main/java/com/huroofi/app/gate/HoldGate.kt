package com.huroofi.app.gate

sealed interface HoldState {
    data object Idle : HoldState

    /** [progress] runs from 0 to just under 1 while the finger is down. */
    data class Holding(val progress: Float) : HoldState

    data object Unlocked : HoldState
}

/** Press-and-hold gate for grown-ups. Pure: time is passed in, so tests need no clock. */
class HoldGate(private val holdMillis: Long = 3_000) {
    var state: HoldState = HoldState.Idle
        private set

    private var pressedAt: Long? = null

    fun press(nowMillis: Long) {
        if (state != HoldState.Unlocked) pressedAt = nowMillis
    }

    fun release() {
        pressedAt = null
        if (state != HoldState.Unlocked) state = HoldState.Idle
    }

    fun tick(nowMillis: Long) {
        if (state == HoldState.Unlocked) return
        val start = pressedAt ?: return
        val held = nowMillis - start
        state = if (held >= holdMillis) HoldState.Unlocked else HoldState.Holding(held.toFloat() / holdMillis)
    }
}
