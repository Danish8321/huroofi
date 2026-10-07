package com.huroofi.app.ui.components

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.platform.LocalContext

/** True when the phone's animator scale is 0, which the "Remove animations" setting sets. */
fun isCalmMotion(animatorScale: Float): Boolean = animatorScale == 0f

/**
 * Whether the phone asks for calm motion (plan 14 decision 3). Compose already snaps animations to
 * their end then; this lets a screen also leave out decoration that would sit frozen, like confetti.
 */
@Composable
fun calmMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        isCalmMotion(Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f))
    }
}

/** Runs animations at their real speed whatever the phone's animator scale, for motion that teaches. */
object TeachingMotion : MotionDurationScale {
    override val scaleFactor: Float = 1f
}
