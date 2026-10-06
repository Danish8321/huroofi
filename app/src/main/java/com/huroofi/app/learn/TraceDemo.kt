package com.huroofi.app.learn

/** What happens in Trace that the demo ball cares about (plan 09 decision 4). */
sealed interface DemoEvent {
    /** The screen opened; the letter sound starts playing. */
    data object Enter : DemoEvent

    /** The letter sound finished. */
    data object SoundDone : DemoEvent

    /** A finger went down on the canvas. */
    data object Touch : DemoEvent

    /** New ink landed. */
    data object Ink : DemoEvent

    /** [ms] passed with the demo not playing. */
    data class Idle(val ms: Long) : DemoEvent

    /** The helper picture ("Show me how") was tapped. */
    data object HelperTap : DemoEvent

    /** "Again" cleared the canvas. */
    data object Again : DemoEvent

    /** Every stroke is finished. */
    data object Done : DemoEvent
}

/** What the ball should do after an event: keep going as it is, stop, show every stroke, or only the next unfinished one. */
enum class DemoPlay { Keep, Stop, All, Next }

/** Quiet time before the ball hints at the next stroke, and again after each further wait. */
const val DEMO_IDLE_MS = 5_000L

data class DemoState(
    val soundDone: Boolean = false,
    val touched: Boolean = false,
    val done: Boolean = false,
    val idleMs: Long = 0,
)

/**
 * Pure schedule of the demo ball. The caller sends [DemoEvent.Idle] only while the ball is not
 * moving, so the wait counts from the end of a demo. No event produces a fail state.
 */
fun reduceDemo(state: DemoState, event: DemoEvent): Pair<DemoState, DemoPlay> = when (event) {
    DemoEvent.Enter -> DemoState() to DemoPlay.Keep
    DemoEvent.SoundDone ->
        if (state.touched || state.done) state.copy(soundDone = true) to DemoPlay.Keep
        else state.copy(soundDone = true, idleMs = 0) to DemoPlay.All
    DemoEvent.Touch -> state.copy(touched = true, idleMs = 0) to DemoPlay.Stop
    DemoEvent.Ink -> state.copy(touched = true, idleMs = 0) to DemoPlay.Keep
    is DemoEvent.Idle ->
        if (!state.soundDone || state.done) {
            state to DemoPlay.Keep
        } else {
            val idle = state.idleMs + event.ms
            if (idle >= DEMO_IDLE_MS) state.copy(idleMs = 0) to DemoPlay.Next else state.copy(idleMs = idle) to DemoPlay.Keep
        }
    DemoEvent.HelperTap -> state.copy(idleMs = 0) to DemoPlay.All
    DemoEvent.Again -> DemoState(soundDone = state.soundDone) to DemoPlay.All
    DemoEvent.Done -> state.copy(done = true, idleMs = 0) to DemoPlay.Stop
}
