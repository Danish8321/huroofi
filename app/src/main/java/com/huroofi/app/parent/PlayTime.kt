package com.huroofi.app.parent

import com.huroofi.app.Routes
import com.huroofi.app.data.progress.limitReached
import com.huroofi.app.learn.LearnRoutes

/**
 * Screens the child plays on. Only these count towards Daily play time and only these are
 * replaced by the Rest screen (plan 06 decision 6; learn screens: plan 07 decision 9).
 */
val ChildRoutes = setOf(
    LearnRoutes.Home,
    LearnRoutes.Lesson,
    LearnRoutes.Trace,
    LearnRoutes.Quiz,
    LearnRoutes.Reward,
    LearnRoutes.Stickers,
    Routes.ToddlerHome,
    Routes.ToddlerCards,
    Routes.ToddlerFind,
    Routes.ToddlerPaint,
)

enum class RestMove { None, ToRest, LeaveRest }

/** Where to go when the route or the limit changes. */
fun restMove(route: String?, limitReached: Boolean): RestMove = when {
    route in ChildRoutes && limitReached -> RestMove.ToRest
    route == Routes.Rest && !limitReached -> RestMove.LeaveRest
    else -> RestMove.None
}

/** Stored seconds plus the ones not saved yet, so the Rest screen is never late by a save interval. */
fun limitReached(storedSeconds: Int, pendingSeconds: Int, limitMinutes: Int): Boolean =
    limitReached(storedSeconds + pendingSeconds, limitMinutes)

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
