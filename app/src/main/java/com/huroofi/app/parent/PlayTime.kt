package com.huroofi.app.parent

import com.huroofi.app.Routes
import com.huroofi.app.data.progress.limitReached
import com.huroofi.app.learn.LearnRoutes

/**
 * Screens the child plays on. Only these count towards Daily play time and only these are
 * replaced by the Rest screen (plan 06 decision 6; learn screens: plan 07 decision 9; Trace
 * practice: plan 15 decision 3).
 */
val ChildRoutes = setOf(
    LearnRoutes.Home,
    LearnRoutes.Lesson,
    LearnRoutes.Trace,
    LearnRoutes.Practice,
    LearnRoutes.Quiz,
    LearnRoutes.Reward,
    LearnRoutes.Stickers,
    LearnRoutes.Map,
    Routes.ToddlerHome,
    Routes.ToddlerCards,
    Routes.ToddlerFind,
    Routes.ToddlerPaint,
)

enum class RestMove { None, ToRest, LeaveRest }

/** How long the game open when time runs out may go on before Rest (plan 16 slice 16). */
const val GRACE_SECONDS = 180

/**
 * Where to go when the route or the time changes. [graceRoute] is the screen that was open when a
 * second of play ran the time out: it is not cut off, but any other child screen goes to Rest, and
 * so does it after [GRACE_SECONDS].
 */
fun restMove(route: String?, usedSeconds: Int, limitMinutes: Int, graceRoute: String? = null): RestMove {
    val reached = limitReached(usedSeconds, limitMinutes)
    val graceOver = limitReached(usedSeconds - GRACE_SECONDS, limitMinutes)
    return when {
        route in ChildRoutes && reached && (route != graceRoute || graceOver) -> RestMove.ToRest
        route == Routes.Rest && !reached -> RestMove.LeaveRest
        else -> RestMove.None
    }
}

/** Counts seconds of play and hands them out in batches for saving. */
class UsageMeter(private val flushEvery: Int = FLUSH_EVERY_SECONDS) {
    var pending: Int = 0
        private set

    /** One more second. Returns the seconds to save once [flushEvery] have built up, else null. */
    fun tick(): Int? {
        pending++
        return if (pending >= flushEvery) drain() else null
    }

    /** Everything not saved yet; resets to zero. */
    fun drain(): Int = pending.also { pending = 0 }

    companion object {
        const val FLUSH_EVERY_SECONDS = 10
    }
}
