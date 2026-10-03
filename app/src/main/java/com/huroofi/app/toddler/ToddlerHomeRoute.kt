package com.huroofi.app.toddler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips

/**
 * Toddler Home with its sound: "ماذا نلعب؟" plays every time Home appears and on a bubble tap;
 * leaving cuts it (plan 05 decision 12). Back is handled by [ToddlerBackHandler].
 */
@Composable
fun ToddlerHomeRoute(onOpenActivity: (ToddlerActivity) -> Unit, onRequestParentZone: () -> Unit) {
    val sound = LocalAppContainer.current.sound
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(sound, scope) }
    LaunchedEffect(Unit) { prompt.play(Clips.whatShallWePlay) }
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
