package com.huroofi.app.gate

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.LockIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors

/** Colours of the parent gate (`ParentGate.html`). */
object GateSpec {
    val HoldPad = Color(0xFF1D3A66)
    val HoldRingTrack = Color(0xFF2B4C7E)
    val GateBody = Color(0xFFD5E2F0)

    /** The pad is named by its "Hold" label; the ring track is the background of the progress arc. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Sun, HuroofiTokens.GateBg, large = true, "lock icon"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.GateBg, large = true, "Grown-ups only"),
        ContrastPair(GateBody, HuroofiTokens.GateBg, large = false, "instructions"),
        ContrastPair(HuroofiTokens.Card, HoldPad, large = true, "Hold label"),
        ContrastPair(HuroofiTokens.Sun, HoldRingTrack, large = true, "hold progress arc"),
        ContrastPair(HuroofiTokens.Sun, HuroofiTokens.GateBg, large = false, "Back to play"),
    ) + ButtonKind.Primary.contrastPairs() + ButtonKind.Sun.contrastPairs()
}

/**
 * Parent gate: press and hold the circle for 3 seconds. Both actions appear only after the hold;
 * "Back to play" is always there (ParentGate.html). Not unit-tested (drawing and touch); the hold rules live in [HoldGate].
 */
@Composable
fun ParentGateScreen(
    onOpenParentZone: () -> Unit,
    onCloseApp: () -> Unit,
    onBackToPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalHuroofiColors.current
    val gate = remember { HoldGate() }
    var state by remember { mutableStateOf<HoldState>(HoldState.Idle) }
    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(pressed) {
        while (pressed && state != HoldState.Unlocked) {
            val now = withFrameMillis { it }
            gate.tick(now)
            state = gate.state
        }
    }

    CappedWidth(colors.gateBg, modifier) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(start = 28.dp, end = 28.dp, top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LockIcon(color = colors.sun, size = 56.dp)
            Text(
                "Grown-ups only",
                style = HuroofiText.screenTitle,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp),
            )
            Text(
                "Press and hold the circle for 3 seconds to open settings.",
                style = HuroofiText.body,
                color = GateSpec.GateBody,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 56.dp)
                    .size(200.dp)
                    .background(GateSpec.HoldPad, CircleShape)
                    .semantics { contentDescription = "Press and hold for 3 seconds" }
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            gate.press(withFrameMillis { it })
                            pressed = true
                            tryAwaitRelease()
                            pressed = false
                            gate.release()
                            state = gate.state
                        })
                    },
                contentAlignment = Alignment.Center,
            ) {
                val progress = when (val s = state) {
                    HoldState.Idle -> 0f
                    is HoldState.Holding -> s.progress
                    HoldState.Unlocked -> 1f
                }
                Canvas(Modifier.size(200.dp)) {
                    val stroke = 14.dp.toPx()
                    val inset = stroke / 2
                    val arc = Size(size.width - stroke, size.height - stroke)
                    drawArc(GateSpec.HoldRingTrack, 0f, 360f, false, Offset(inset, inset), arc, style = Stroke(stroke))
                    drawArc(
                        colors.sun, -90f, 360f * progress, false, Offset(inset, inset), arc,
                        style = Stroke(stroke, cap = StrokeCap.Round),
                    )
                }
                Text(
                    when (state) {
                        HoldState.Idle -> "Hold"
                        is HoldState.Holding -> "Keep holding…"
                        HoldState.Unlocked -> "Unlocked"
                    },
                    style = HuroofiText.sectionHeading,
                    color = Color.White,
                )
            }
            Column(
                modifier = Modifier.padding(top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                for (action in gateActions(state)) {
                    when (action) {
                        GateAction.OpenParentZone -> PrimaryButton("Open Parent zone", onClick = onOpenParentZone)
                        GateAction.CloseApp -> PrimaryButton("Close Huroofi", onClick = onCloseApp, kind = ButtonKind.Sun)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.defaultMinSize(minHeight = HuroofiDimens.MinTouch).clickable(role = Role.Button, onClick = onBackToPlay),
                contentAlignment = Alignment.Center,
            ) {
                Text("Back to play", style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = colors.sun)
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun ParentGatePreview() {
    HuroofiTheme { ParentGateScreen(onOpenParentZone = {}, onCloseApp = {}, onBackToPlay = {}) }
}
