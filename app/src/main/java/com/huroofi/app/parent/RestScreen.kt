package com.huroofi.app.parent

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.R
import com.huroofi.app.audio.Clips
import com.huroofi.app.toddler.PARENT_LOCK_DESCRIPTION
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ParentLock
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens

/** Rest screen (plan 06 decision 6, `Rest.html`). A child screen: no timer, no red, the Parent lock is the only control. */
object RestSpec {
    val Lion = 200.dp
    val Moon = 64.dp
    val NightTop = Color(0xFF24345E)
    val NightBottom = Color(0xFF3A4F86)
    val Moonlight = Color(0xFFFFE07A)
    val Body = Color(0xFFC9DBF2)

    /** The prototype's 14 % white over the night sky, made solid so the sweep can check it. */
    val LockFace = Color(0xFF435075)
    const val ARABIC_SP = 48f
    const val BODY_SP = 20f

    /** No toddler controls: the Parent lock is the named exception in the sweep. */
    val touchSizes = emptyList<Dp>()
    val colors = listOf(NightTop, NightBottom, Moonlight, Body, LockFace, HuroofiTokens.Card)
    const val CHOICES = 0

    /** Checked on the lighter bottom of the sky, the worse case for light text. */
    val textPairs = listOf(
        ContrastPair(HuroofiTokens.Card, NightBottom, large = true, "rest Arabic"),
        ContrastPair(Body, NightBottom, large = true, "rest English"),
        ContrastPair(Body, LockFace, large = true, "parent lock icon"),
    )
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

private val MoonPath = ButtonIcons.Moon.single()

/** Night falls: Leo under a moon and a few stars, and the only button is the grown-ups' lock. */
@Composable
fun RestScreen(onRequestParentZone: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(RestSpec.NightTop, RestSpec.NightBottom)))) {
        Box(Modifier.fillMaxSize().safeDrawingPadding()) {
            MoonIcon(Modifier.padding(start = 34.dp, top = 70.dp).size(RestSpec.Moon))
            Row(
                Modifier.align(Alignment.TopEnd).padding(end = 70.dp, top = 130.dp).alpha(0.9f),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                for (size in listOf(22.dp, 16.dp, 26.dp)) StarIcon(RestSpec.Moonlight, size = size, outline = HuroofiTokens.SunShadow)
            }
            ParentLock(
                onRequestParentZone = onRequestParentZone,
                contentDescription = PARENT_LOCK_DESCRIPTION,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 14.dp, end = 14.dp),
                face = RestSpec.LockFace,
                icon = RestSpec.Body,
            )
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
            ) {
                // A little less colour, as in the prototype: Leo is sleepy too.
                Image(
                    painterResource(R.drawable.pic_lion),
                    contentDescription = null,
                    modifier = Modifier.size(RestSpec.Lion),
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.85f) }),
                )
                ArabicText("وقت الراحة", size = RestSpec.ARABIC_SP.sp, color = HuroofiTokens.Card)
                Text(
                    "Time to rest. See you tomorrow!",
                    style = HuroofiText.body.copy(fontSize = RestSpec.BODY_SP.sp, fontWeight = FontWeight.Bold),
                    color = RestSpec.Body,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun MoonIcon(modifier: Modifier) {
    val path = remember { PathParser().parsePathString(MoonPath).toPath() }
    Canvas(modifier) {
        val u = size.width / 24f
        scale(u, u, pivot = Offset.Zero) {
            drawPath(path, RestSpec.Moonlight)
            drawPath(path, RestSpec.Moonlight, style = Stroke(width = 2.2f, join = StrokeJoin.Round))
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RestPreview() {
    HuroofiTheme { RestScreen(onRequestParentZone = {}) }
}

