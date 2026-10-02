package com.huroofi.app.data.progress

/** Parent-chosen learning mode. Default is TODDLER. */
enum class AgeMode {
    TODDLER, PRESCHOOL, READER;

    companion object {
        fun parse(value: String?): AgeMode = entries.firstOrNull { it.name == value } ?: TODDLER
    }
}

const val MIN_LIMIT_MINUTES = 5
const val MAX_LIMIT_MINUTES = 60
const val LIMIT_STEP_MINUTES = 5
const val DEFAULT_LIMIT_MINUTES = 20

/** [stageOf] maps letter index to stage number (derived from letters.json by the caller). */
fun isStageComplete(stage: Int, completed: Set<Int>, stageOf: Map<Int, Int>): Boolean {
    val inStage = stageOf.filterValues { it == stage }.keys
    return inStage.isNotEmpty() && completed.containsAll(inStage)
}

/** Highest stage that is open: stage 1 always, stage n+1 once every letter of stage n is complete. */
fun unlockedStage(completed: Set<Int>, stageOf: Map<Int, Int>): Int {
    val last = stageOf.values.maxOrNull() ?: return 1
    var open = 1
    while (open < last && isStageComplete(open, completed, stageOf)) open++
    return open
}

fun limitReached(usageSeconds: Int, limitMinutes: Int): Boolean = usageSeconds >= limitMinutes * 60

/** Stored usage counts only for the day it was recorded; a new day starts at zero. */
fun usageForToday(storedDay: String?, storedSeconds: Int, today: String): Int =
    if (storedDay == today) storedSeconds.coerceAtLeast(0) else 0

/** Snaps to the nearest step of 5 inside 5..60. */
fun clampLimitMinutes(minutes: Int): Int {
    val snapped = ((minutes + LIMIT_STEP_MINUTES / 2) / LIMIT_STEP_MINUTES) * LIMIT_STEP_MINUTES
    return snapped.coerceIn(MIN_LIMIT_MINUTES, MAX_LIMIT_MINUTES)
}
