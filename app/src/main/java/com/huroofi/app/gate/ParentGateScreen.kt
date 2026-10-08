package com.huroofi.app.gate

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.learn.dropEdge
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors

/** Sizes and colours of the parent gate (`ParentGate.html`). */
object GateSpec {
    val BackCard = 84.dp
    val BackIcon = 56.dp
    val BackShadow = Color(0xFF0A1A33)
    val Chip = Color(0xFF1F3C68)
    val ChipText = Color(0xFFA9CBF2)
    val Body = Color(0xFFC9DBF2)
    val Foot = Color(0xFF8FA9CC)
    val Hold = 220.dp
    val HoldPad = 148.dp
    val HoldTrack = Color(0xFF1F3C68)
    val HoldRingTrack = Color(0xFF2B4F86)
    val GhostBorder = Color(0xFF3A5A8C)
    const val CHIP_SP = 15f
    const val TITLE_SP = 28f
    val textSizes = listOf(CHIP_SP, TITLE_SP)

    val touchSizes = listOf(BackCard, HoldPad, HuroofiDimens.PrimaryButtonHeight)
    val colors = listOf(BackShadow, Chip, ChipText, Body, Foot, HoldTrack, HoldRingTrack, GhostBorder, HuroofiTokens.GateBg, HuroofiTokens.Sun)

    /** A child who lands here sees two things: the way back and the circle. */
    const val CHOICES = 2

    /** The progress arc runs over its track; both sit on the gate background. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "Back to play"),
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Sky, large = true, "home icon"),
        ContrastPair(ChipText, Chip, large = false, "GROWN-UPS ONLY"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.GateBg, large = true, "heading"),
        ContrastPair(Body, HuroofiTokens.GateBg, large = false, "instructions"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "Hold label and hand"),
        ContrastPair(HuroofiTokens.Sun, HoldRingTrack, large = true, "hold progress arc"),
        ContrastPair(Body, HuroofiTokens.GateBg, large = true, "Close Huroofi"),
        ContrastPair(Foot, HuroofiTokens.GateBg, large = false, "letting go hint"),
    ) + ButtonKind.Sun.contrastPairs()
}

/**
 * Parent gate: hold the sun circle for 2 seconds. Before the hold, "Back to play" is the only other
 * control; after it, the circle gives way to "Open Parent zone" and "Close Huroofi" (ParentGate.html).
 * Not unit-tested (drawing and touch); the hold rules live in [HoldGate].
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
    val unlocked = state == HoldState.Unlocked

    CappedWidth(colors.gateBg, modifier) {
        // Scrolls when a large text size outgrows a short window (tablet landscape, plan 08 task 6.2);
        // at least the window tall, so the hint stays at the bottom when everything fits.
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BackToPlay(onBackToPlay)
                Text(
                    "GROWN-UPS ONLY",
                    Modifier.padding(top = 50.dp).background(GateSpec.Chip, CircleShape).padding(horizontal = 14.dp, vertical = 4.dp),
                    style = HuroofiText.body.copy(fontSize = GateSpec.CHIP_SP.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.9.sp),
                    color = GateSpec.ChipText,
                )
                Text(
                    if (unlocked) "Unlocked" else "Hold to open settings",
                    Modifier.padding(top = 16.dp),
                    style = HuroofiText.screenTitle.copy(fontSize = GateSpec.TITLE_SP.sp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Text(
                    if (unlocked) "Choose where to go." else "Keep your finger on the circle for 2 seconds.",
                    Modifier.padding(top = 10.dp).widthIn(max = 290.dp),
                    style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold),
                    color = GateSpec.Body,
                    textAlign = TextAlign.Center,
                )
                if (unlocked) {
                    Column(Modifier.padding(top = 40.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        for (action in gateActions(state)) {
                            when (action) {
                                GateAction.OpenParentZone ->
                                    PrimaryButton("Open Parent zone", onClick = onOpenParentZone, kind = ButtonKind.Sun, icon = ButtonIcons.Next)
                                GateAction.CloseApp -> CloseButton(onCloseApp)
                            }
                        }
                    }
                } else {
                    HoldCircle(
                        state = state,
                        onPress = {
                            gate.press(it)
                            pressed = true
                        },
                        onRelease = {
                            pressed = false
                            gate.release()
                            state = gate.state
                        },
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    if (unlocked) "" else "Letting go starts the circle again.",
                    Modifier.padding(top = 24.dp).heightIn(min = 22.dp),
                    style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold),
                    color = GateSpec.Foot,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** The way back for a child who tapped the lock by mistake: a big white card, always there. */
@Composable
private fun BackToPlay(onClick: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .height(GateSpec.BackCard)
            .dropEdge(GateSpec.BackShadow, 6.dp, shape)
            .clip(shape)
            .background(HuroofiTokens.Card)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(Modifier.size(GateSpec.BackIcon).background(HuroofiTokens.Sky, CircleShape), contentAlignment = Alignment.Center) {
            LineIcon(ButtonIcons.Home, HuroofiTokens.Primary, size = 30.dp, strokeWidth = 2.2f)
        }
        Text("Back to play", Modifier.weight(1f), style = HuroofiText.buttonPrimary, color = HuroofiTokens.Navy)
        Image(painterResource(R.drawable.pic_lion), contentDescription = null, modifier = Modifier.size(GateSpec.BackIcon))
    }
}

@Composable
private fun HoldCircle(state: HoldState, onPress: (Long) -> Unit, onRelease: () -> Unit) {
    val progress = when (state) {
        HoldState.Idle -> 0f
        is HoldState.Holding -> state.progress
        HoldState.Unlocked -> 1f
    }
    val held = state is HoldState.Holding
    Box(
        Modifier
            .padding(top = 40.dp)
            .size(GateSpec.Hold)
            .semantics { contentDescription = "Press and hold for 2 seconds" }
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    onPress(withFrameMillis { it })
                    tryAwaitRelease()
                    onRelease()
                })
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(GateSpec.Hold)) {
            val stroke = 14.dp.toPx()
            val r = size.minDimension / 2 - stroke / 2
            drawCircle(GateSpec.HoldTrack, r)
            drawCircle(GateSpec.HoldRingTrack, r, style = Stroke(stroke))
            drawArc(
                HuroofiTokens.Sun, -90f, 360f * progress, false,
                Offset(center.x - r, center.y - r), Size(r * 2, r * 2),
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        // The sun pad sinks onto its edge while held, like a pressed button.
        Box(Modifier.size(GateSpec.HoldPad).offset(y = 7.dp).background(HuroofiTokens.SunShadow, CircleShape))
        Column(
            Modifier.size(GateSpec.HoldPad).offset(y = if (held) 7.dp else 0.dp).background(HuroofiTokens.Sun, CircleShape),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            LineIcon(ButtonIcons.Hand, HuroofiTokens.Navy, size = 44.dp, strokeWidth = 2f)
            Text(
                if (held) "Keep holding…" else "Hold",
                style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold),
                color = HuroofiTokens.Navy,
            )
        }
    }
}

/** Outlined, so the sun "Open Parent zone" stays the main way on. */
@Composable
private fun CloseButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(HuroofiDimens.PrimaryButtonCorner)
    Row(
        Modifier
            .fillMaxWidth()
            .height(HuroofiDimens.PrimaryButtonHeight)
            .clip(shape)
            .border(2.dp, GateSpec.GhostBorder, shape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LineIcon(ButtonIcons.Exit, GateSpec.Body, size = 22.dp, strokeWidth = 2.2f)
        Text("Close Huroofi", style = HuroofiText.buttonPrimary.copy(fontSize = 18.sp), color = GateSpec.Body)
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun ParentGatePreview() {
    HuroofiTheme { ParentGateScreen(onOpenParentZone = {}, onCloseApp = {}, onBackToPlay = {}) }
}
