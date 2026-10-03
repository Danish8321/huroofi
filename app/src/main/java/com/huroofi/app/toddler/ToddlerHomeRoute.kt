package com.huroofi.app.toddler

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips

/**
 * Toddler Home with its sound: "ماذا نلعب؟" plays every time Home appears and on a bubble tap;
 * leaving cuts it (plan 05 decision 12). Back does nothing here (decision 6).
 */
@Composable
fun ToddlerHomeRoute(onOpenActivity: (ToddlerActivity) -> Unit, onRequestParentZone: () -> Unit) {
    val sound = LocalAppContainer.current.sound
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(sound, scope) }
    LaunchedEffect(Unit) { prompt.play(Clips.whatShallWePlay) }
    BackHandler {}
    ToddlerHomeScreen(
        onReplayPrompt = { prompt.play(Clips.whatShallWePlay) },
        onOpenActivity = {
            prompt.stop()
            onOpenActivity(it)
        },
        onRequestParentZone = {
            prompt.stop()
            onRequestParentZone()
        },
    )
}
