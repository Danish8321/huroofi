package com.huroofi.app.learn

/** What system Back does on a Preschool / Early reader screen (plan 07 decision 9). */
enum class LearnBack { ToGate, ToLesson, ToMap, Default }

/**
 * Learn screens are always opened on top of Home (the back stack is Home plus one screen), so Default
 * lands on Home. Home itself opens the Parent gate: leaving the app is guarded.
 */
fun learnBack(route: String?): LearnBack = when (route) {
    LearnRoutes.Home -> LearnBack.ToGate
    LearnRoutes.Trace -> LearnBack.ToLesson
    LearnRoutes.Reward -> LearnBack.ToMap
    else -> LearnBack.Default
}

/** Route patterns of the learning path. Arguments: letter index, or stage number for Reward. */
object LearnRoutes {
    const val Home = "home"
    const val Map = "map"
    const val Stickers = "stickers"
    const val Lesson = "lesson/{index}"
    const val Trace = "trace/{index}"
    const val Quiz = "quiz/{index}"
    const val Reward = "reward/{stage}"
    const val ARG_INDEX = "index"
    const val ARG_STAGE = "stage"

    fun lesson(index: Int) = "lesson/$index"
    fun trace(index: Int) = "trace/$index"
    fun quiz(index: Int) = "quiz/$index"
    fun reward(stage: Int) = "reward/$stage"
}
