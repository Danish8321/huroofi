package com.huroofi.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Vibration (plan 14 decision 4): a gentle buzz when the child gets something right, if the
 * Parent-zone switch is on. There is deliberately nothing for a wrong answer (no fail states).
 */
class Buzz(private val haptic: HapticFeedback?, private val enabled: Boolean) {
    /** A finished Trace stroke. */
    fun tick() = perform(HapticFeedbackType.SegmentTick)

    /** A traced letter, a right Quiz or Find answer, a Paint star, a new sticker. */
    fun confirm() = perform(HapticFeedbackType.Confirm)

    private fun perform(type: HapticFeedbackType) {
        if (enabled) haptic?.performHapticFeedback(type)
    }
}

/** No buzz until [ProvideBuzz] supplies one, so previews and tests stay still. */
val LocalBuzz = staticCompositionLocalOf { Buzz(null, enabled = false) }

@Composable
fun ProvideBuzz(enabled: Boolean, content: @Composable () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val buzz = remember(haptic, enabled) { Buzz(haptic, enabled) }
    CompositionLocalProvider(LocalBuzz provides buzz, content = content)
}
