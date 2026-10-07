package com.huroofi.app.learn

import org.junit.Assert.assertEquals
import org.junit.Test

class LearnBackTest {
    @Test
    fun homeOpensTheGate() {
        assertEquals(LearnBack.ToGate, learnBack(LearnRoutes.Home))
    }

    @Test
    fun traceGoesBackToItsLesson() {
        assertEquals(LearnBack.ToLesson, learnBack(LearnRoutes.Trace))
    }

    @Test
    fun rewardGoesToTheMap() {
        assertEquals(LearnBack.ToMap, learnBack(LearnRoutes.Reward))
    }

    @Test
    fun othersPopToHome() {
        for (r in listOf(LearnRoutes.Lesson, LearnRoutes.Practice, LearnRoutes.Quiz, LearnRoutes.Map, LearnRoutes.Stickers, null)) {
            assertEquals("$r", LearnBack.Default, learnBack(r))
        }
    }

    @Test
    fun routeBuildersMatchPatterns() {
        assertEquals("lesson/3", LearnRoutes.lesson(3))
        assertEquals("trace/3", LearnRoutes.trace(3))
        assertEquals("practice/3", LearnRoutes.practice(3))
        assertEquals("quiz/3", LearnRoutes.quiz(3))
        assertEquals("reward/2", LearnRoutes.reward(2))
    }
}
