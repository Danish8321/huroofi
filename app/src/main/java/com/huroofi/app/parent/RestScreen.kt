package com.huroofi.app.parent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.R
import com.huroofi.app.audio.Clips
import com.huroofi.app.toddler.PARENT_LOCK_DESCRIPTION
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ParentLock
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens

/** Rest screen (plan 06 decision 6). A child screen: no timer, no red, the Parent lock is the only control. */
object RestSpec {
    val Lion = 220.dp

    /** No toddler controls: the Parent lock is the named exception in the sweep. */
    val touchSizes = emptyList<Dp>()
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Navy, HuroofiTokens.Muted)
    const val CHOICES = 0
    val textPairs = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "rest Arabic"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "rest English"),
    ) + parentLockPairs
}

/** Plays the rest line once when shown, cutting whatever was playing. */
@Composable
fun RestRoute(onRequestParentZone: () -> Unit) {
    val sound = LocalAppContainer.current.sound
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(sound, scope) }
    LaunchedEffect(Unit) { prompt.play(Clips.timeToRest) }
    RestScreen(
        onRequestParentZone = {
            prompt.stop()
            onRequestParentZone()
        },
    )
}

@Composable
fun RestScreen(onRequestParentZone: () -> Unit) {
    ToddlerScaffold {
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            ParentLock(onRequestParentZone = onRequestParentZone, contentDescription = PARENT_LOCK_DESCRIPTION)
        }
        Column(
            Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Image(painterResource(R.drawable.pic_lion), contentDescription = null, modifier = Modifier.size(RestSpec.Lion))
            ArabicText("وقت الراحة", size = HuroofiText.toddlerWord.fontSize, color = HuroofiTokens.Navy)
            Text(
                "Time to rest. See you tomorrow!",
                style = HuroofiText.body,
                color = HuroofiTokens.Muted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RestPreview() {
    HuroofiTheme { RestScreen(onRequestParentZone = {}) }
}

