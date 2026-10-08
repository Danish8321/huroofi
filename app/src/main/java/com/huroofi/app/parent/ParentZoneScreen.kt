package com.huroofi.app.parent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.data.content.Stage
import com.huroofi.app.data.progress.AgeMode
import com.huroofi.app.data.progress.LIMIT_STEP_MINUTES
import com.huroofi.app.data.progress.MAX_LIMIT_MINUTES
import com.huroofi.app.data.progress.MIN_LIMIT_MINUTES
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.NotoNaskhArabic

/** Sizes and colours of the Parent zone, from `Parents.html` (plan 06 decision 9). */
object ZoneSpec {
    val PagePadding = 18.dp
    val Gap = 14.dp
    val CardRadius = 22.dp
    val CardPadding = 16.dp
    val CardBorder = Color(0xFFD5E2F0)
    /** "Back to kid mode": a small primary button, as everywhere grown-ups act. */
    val BackButton = 48.dp
    val SmallCorner = 14.dp
    val GhostBorder = Color(0xFFA9CBF2)
    /** Start over is final, so its confirm button is the one dark orange in the app (grown-ups only). */
    val Warn = Color(0xFFA3361A)
    val Soon = Color(0xFF8A5A00)
    val Avatar = 52.dp
    val AvatarFill = Color(0xFFFFF4D6)
    val BarHeight = 12.dp
    val BarTrack = Color(0xFFE3ECF7)
    val Legend = 14.dp
    val Learned = HuroofiTokens.Success
    val Learning = HuroofiTokens.Primary
    /** Outlines the bar and the to-go swatch, so the light track still shows on white; also the empty radio and the off switch (the prototype's #9AAAC0 is under 3:1). */
    val ToGoOutline = Color(0xFF7D8FA8)
    val Cell = 42.dp
    val CellGap = 6.dp
    val CellRadius = 12.dp
    val CellRing = 3.dp
    val CellGlyph = 26.sp
    val ToGoCell = Color(0xFFEEF3F9)
    const val GRID_COLUMNS = 7
    val ModeButton = 72.dp
    val ModeRadius = 18.dp
    val ModeBorder = 2.dp
    val ModeEdge = Color(0xFFE3ECF7)
    val ModeOnFill = Color(0xFFEEF5FF)
    val Radio = 26.dp
    val Row = 72.dp
    val Divider = Color(0xFFEEF3F9)
    val SwitchWidth = 60.dp
    val SwitchHeight = 36.dp
    val Knob = 28.dp
    val SwitchOn = HuroofiTokens.Success
    val SwitchOff = Color(0xFF7D8FA8)
    const val DISABLED_ALPHA = 0.5f
    val Stepper = 48.dp
    val StepperRadius = 12.dp
    val StepperValue = 54.dp
    val Title = 26.sp

    /** Sizes set here; everything else uses HuroofiText styles, which TypeScaleTest keeps ≥ 16 sp. */
    val textSizes = listOf(Title, CellGlyph)

    val touchSizes = listOf(BackButton, ModeButton, Row, Stepper)

    /** Disabled rows and steppers are inactive, so they are not checked. Text up to 18 sp counts as normal. */
    val textPairs = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.ParentBg, large = false, "title, unselected mode"),
        ContrastPair(HuroofiTokens.Navy, Color.White, large = false, "card text"),
        ContrastPair(HuroofiTokens.Muted, Color.White, large = false, "captions"),
        ContrastPair(Color.White, Learned, large = false, "learned grid letter"),
        ContrastPair(HuroofiTokens.Navy, ModeOnFill, large = false, "selected mode"),
        ContrastPair(HuroofiTokens.Muted, ModeOnFill, large = false, "selected mode caption"),
        ContrastPair(HuroofiTokens.Primary, ModeOnFill, large = true, "selected mode ring and radio"),
        ContrastPair(ToGoOutline, Color.White, large = true, "empty radio"),
        ContrastPair(Color.White, HuroofiTokens.Primary, large = false, "Back to kid mode"),
        ContrastPair(HuroofiTokens.PrimaryShadow, Color.White, large = false, "ghost buttons and steppers"),
        ContrastPair(Color.White, Warn, large = false, "Start over, confirmed"),
        ContrastPair(Soon, Color.White, large = false, "coming soon note"),
        ContrastPair(HuroofiTokens.Success, HuroofiTokens.ParentBg, large = true, "trust shield"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.ParentBg, large = false, "trust line"),
        ContrastPair(HuroofiTokens.Muted, ToGoCell, large = false, "to-go grid letter"),
        ContrastPair(Learned, BarTrack, large = true, "progress bar, learned"),
        ContrastPair(Learning, BarTrack, large = true, "progress bar, learning"),
        ContrastPair(ToGoOutline, Color.White, large = true, "progress bar and to-go swatch outline"),
        ContrastPair(Learned, Color.White, large = true, "legend swatch, learned"),
        ContrastPair(Learning, Color.White, large = true, "legend swatch, learning"),
        ContrastPair(Learning, Color.White, large = true, "learning grid ring"),
        ContrastPair(SwitchOn, Color.White, large = true, "switch track, on"),
        ContrastPair(SwitchOff, Color.White, large = true, "switch track, off"),
        ContrastPair(Color.White, SwitchOn, large = true, "switch knob, on"),
        ContrastPair(Color.White, SwitchOff, large = true, "switch knob, off"),
    )
}

/** One cell of the letter grid. */
data class GridLetter(val glyph: String, val name: String, val state: LetterState)

@Composable
fun ParentZoneScreen(
    progress: ParentProgress,
    letters: List<GridLetter>,
    mode: AgeMode,
    onBack: () -> Unit,
    onModeChange: (AgeMode) -> Unit,
    voice: Boolean,
    onVoiceChange: (Boolean) -> Unit,
    harakat: Boolean,
    onHarakatChange: (Boolean) -> Unit,
    limitMinutes: Int,
    onLimitChange: (Int) -> Unit,
    unlockAll: Boolean,
    onUnlockAllChange: (Boolean) -> Unit,
    traceOnly: Boolean,
    onTraceOnlyChange: (Boolean) -> Unit,
    onStartOver: () -> Unit,
    haptics: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    CappedWidth(LocalHuroofiColors.current.parentBg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(ZoneSpec.PagePadding),
            verticalArrangement = Arrangement.spacedBy(ZoneSpec.Gap),
        ) {
            ZoneHeader(onBack)
            ChildCard(progress, mode, letters)
            ModeCard(mode, onModeChange)
            SettingsCard(voice, onVoiceChange, haptics, onHapticsChange, harakat, onHarakatChange, limitMinutes, onLimitChange)
            LessonsCard(unlockAll, onUnlockAllChange, traceOnly, onTraceOnlyChange)
            StartOverCard(onStartOver)
            Row(
                Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LineIcon(ButtonIcons.Shield, HuroofiTokens.Success, size = 20.dp, strokeWidth = 2.2f)
                Text("No ads · no tracking · works offline", style = HuroofiText.caption.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Muted)
            }
            footer()
        }
    }
}

@Composable
private fun ZoneHeader(onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Parent zone",
            style = HuroofiText.screenTitle.copy(fontSize = ZoneSpec.Title),
            color = HuroofiTokens.Navy,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        SmallButton("Back to kid mode", SmallKind.Primary, icon = ButtonIcons.Home, onClick = onBack)
    }
}

@Composable
fun ZoneCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(ZoneSpec.CardRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape)
            .border(1.dp, ZoneSpec.CardBorder, shape)
            .padding(ZoneSpec.CardPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun ChildCard(progress: ParentProgress, mode: AgeMode, letters: List<GridLetter>) {
    ZoneCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painterResource(R.drawable.pic_lion),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ZoneSpec.Avatar)
                    .clip(CircleShape)
                    .background(ZoneSpec.AvatarFill)
                    .border(2.dp, HuroofiTokens.Sun, CircleShape)
                    .scale(1.15f),
            )
            Column {
                Text("Your child", style = HuroofiText.sectionHeading, color = HuroofiTokens.Navy)
                Text(
                    "Stage ${progress.stage.stage} of ${progress.stageCount} · ${progress.stage.name}",
                    style = HuroofiText.caption,
                    color = HuroofiTokens.Muted,
                )
            }
        }
        ProgressBar(progress)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendItem(ZoneSpec.Learned, "${progress.learned} learned")
            LegendItem(Color.White, "${progress.learning} learning", ring = ZoneSpec.Learning)
            LegendItem(ZoneSpec.BarTrack, "${progress.toGo} to go", ring = ZoneSpec.ToGoOutline)
        }
        if (mode == AgeMode.TODDLER) {
            Text(
                "Toddler play isn't tracked. Letters count once lessons start (Preschool mode).",
                style = HuroofiText.caption,
                color = HuroofiTokens.Muted,
            )
        }
        LetterGrid(letters)
    }
}

private data class ModeLabel(val mode: AgeMode, val title: String, val age: String, val what: String)

/** Mode labels from the prototype. A new mode applies on leaving the zone (plan 06 decision 8). */
private val modeLabels = listOf(
    ModeLabel(AgeMode.TODDLER, "Toddler", "18m – 3y", "Picture cards, find-it game, finger paint"),
    ModeLabel(AgeMode.PRESCHOOL, "Preschool", "3 – 5y", "Meet, trace and a 3-picture game per letter"),
    ModeLabel(AgeMode.READER, "Early reader", "5y+", "Adds letter shapes and a 4-picture game"),
)

@Composable
private fun ModeCard(mode: AgeMode, onModeChange: (AgeMode) -> Unit) {
    ZoneCard {
        Column {
            Text("Learning mode", style = HuroofiText.sectionHeading, color = HuroofiTokens.Navy)
            Text(
                "Changes what your child sees when you go back to kid mode",
                style = HuroofiText.caption,
                color = HuroofiTokens.Muted,
            )
        }
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (label in modeLabels) ModeButton(label, label.mode == mode) { onModeChange(label.mode) }
        }
    }
}

@Composable
private fun ModeButton(label: ModeLabel, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(ZoneSpec.ModeRadius)
    val ring = if (selected) HuroofiTokens.Primary else ZoneSpec.ToGoOutline
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = ZoneSpec.ModeButton)
            .clip(shape)
            .background(if (selected) ZoneSpec.ModeOnFill else Color.White)
            .border(ZoneSpec.ModeBorder, if (selected) HuroofiTokens.Primary else ZoneSpec.ModeEdge, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(ZoneSpec.Radio).border(3.dp, ring, CircleShape), contentAlignment = Alignment.Center) {
            if (selected) Box(Modifier.size(12.dp).background(HuroofiTokens.Primary, CircleShape))
        }
        Column(Modifier.weight(1f)) {
            Text(
                buildAnnotatedString {
                    append(label.title)
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = HuroofiTokens.Muted)) { append(" · ${label.age}") }
                },
                style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold),
                color = HuroofiTokens.Navy,
            )
            Text(label.what, style = HuroofiText.caption, color = HuroofiTokens.Muted)
        }
    }
}

@Composable
private fun SettingsCard(
    voice: Boolean,
    onVoiceChange: (Boolean) -> Unit,
    haptics: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    harakat: Boolean,
    onHarakatChange: (Boolean) -> Unit,
    limitMinutes: Int,
    onLimitChange: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(ZoneSpec.CardRadius)
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White, shape)
            .border(1.dp, ZoneSpec.CardBorder, shape)
            .padding(horizontal = ZoneSpec.CardPadding, vertical = 4.dp),
    ) {
        ToggleRow("Voice & sounds", AnnotatedString("Native-speaker audio for every letter"), voice, onVoiceChange)
        ZoneDivider()
        ToggleRow("Vibration", AnnotatedString("A gentle buzz when your child gets it right"), haptics, onHapticsChange)
        ZoneDivider()
        // Plan 07 decision 6: vowelled data is deferred, so the toggle is locked off until it ships.
        ToggleRow("Show vowel marks", harakatSubtitle, harakat, onHarakatChange, enabled = false)
        Text(
            "Coming soon: vowel marks arrive in a later update.",
            Modifier.padding(bottom = 12.dp),
            style = HuroofiText.caption,
            color = ZoneSpec.Soon,
        )
        ZoneDivider()
        SettingRow("Daily play time", AnnotatedString("A calm “time to rest” screen appears when it is used up")) {
            LimitStepper(limitMinutes, onLimitChange)
        }
        ZoneDivider()
        SettingRow("Offline pack", AnnotatedString("All content is on this device."))
        // Plan 08 decision 8: apps cannot block the home gesture; Android's App pinning can.
        Text(
            "To keep your child in Huroofi, turn on App pinning in your phone's Settings.",
            Modifier.padding(bottom = 12.dp),
            style = HuroofiText.caption,
            color = HuroofiTokens.Muted,
        )
    }
}

/** "بَ بِ بُ" in Naskh inside the English subtitle (plan 06 decision 4). */
private val harakatSubtitle = buildAnnotatedString {
    append("Adds vowel marks like ")
    withStyle(SpanStyle(fontFamily = NotoNaskhArabic, fontWeight = FontWeight.Bold)) { append("بَ بِ بُ") }
    append(" in lessons")
}

@Composable
private fun ZoneDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(ZoneSpec.Divider))
}

/** How lessons open (plan 13 decisions 3 and 4). */
@Composable
private fun LessonsCard(unlockAll: Boolean, onUnlockAllChange: (Boolean) -> Unit, traceOnly: Boolean, onTraceOnlyChange: (Boolean) -> Unit) {
    ZoneCard {
        Text("Lessons", style = HuroofiText.sectionHeading, color = HuroofiTokens.Navy, modifier = Modifier.semantics { heading() })
        ToggleRow("Unlock all", AnnotatedString("Start any stage from the Letter map"), unlockAll, onUnlockAllChange)
        ZoneDivider()
        ToggleRow("Trace only", AnnotatedString("Letters open straight into tracing, as practice"), traceOnly, onTraceOnlyChange)
    }
}

/** "Start over" behind a confirm step: it clears learned letters and stickers for good (plan 13 decision 2). */
@Composable
private fun StartOverCard(onStartOver: () -> Unit) {
    var asking by rememberSaveable { mutableStateOf(false) }
    // The question replaces the row in place, as in the prototype, so it is read where it was asked.
    ZoneCard {
        if (asking) {
            Column(Modifier.semantics(mergeDescendants = true) {}) {
                Text("Start over?", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
                Text("This clears every learned letter and sticker. It can't be undone.", style = HuroofiText.caption, color = HuroofiTokens.Muted)
            }
            Row(Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallButton("Cancel", SmallKind.Ghost) { asking = false }
                SmallButton("Start over", SmallKind.Warn) {
                    asking = false
                    onStartOver()
                }
            }
        } else {
            SettingRow("Start over", AnnotatedString("Clears every learned letter and sticker")) {
                SmallButton("Start over", SmallKind.Ghost) { asking = true }
            }
        }
    }
}

private enum class SmallKind { Primary, Ghost, Warn }

/** The prototype's `.btn-small`: 48 dp tall, for grown-ups. */
@Composable
private fun SmallButton(label: String, kind: SmallKind, icon: List<String>? = null, onClick: () -> Unit) {
    val shape = RoundedCornerShape(ZoneSpec.SmallCorner)
    val (fill, text) = when (kind) {
        SmallKind.Primary -> HuroofiTokens.Primary to Color.White
        SmallKind.Ghost -> Color.White to HuroofiTokens.PrimaryShadow
        SmallKind.Warn -> ZoneSpec.Warn to Color.White
    }
    Row(
        Modifier
            .heightIn(min = ZoneSpec.BackButton)
            .clip(shape)
            .background(fill)
            .then(if (kind == SmallKind.Ghost) Modifier.border(1.dp, ZoneSpec.GhostBorder, shape) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) LineIcon(icon, text, size = 20.dp, strokeWidth = 2.2f)
        Text(label, style = HuroofiText.caption.copy(fontWeight = FontWeight.ExtraBold), color = text)
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: AnnotatedString,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().heightIn(min = ZoneSpec.Row).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
            Text(subtitle, style = HuroofiText.caption, color = HuroofiTokens.Muted)
        }
        control()
    }
}

/**
 * The whole row toggles; the drawn switch is decoration (prototype: 56×34 track, 26 knob). A disabled
 * row shows off and faded, whatever is stored.
 */
@Composable
private fun ToggleRow(title: String, subtitle: AnnotatedString, checked: Boolean, onChange: (Boolean) -> Unit, enabled: Boolean = true) {
    val on = checked && enabled
    SettingRow(
        title,
        subtitle,
        Modifier
            .toggleable(value = on, enabled = enabled, role = Role.Switch, onValueChange = onChange)
            .alpha(if (enabled) 1f else ZoneSpec.DISABLED_ALPHA),
    ) {
        Box(
            Modifier
                .size(ZoneSpec.SwitchWidth, ZoneSpec.SwitchHeight)
                .background(if (on) ZoneSpec.SwitchOn else ZoneSpec.SwitchOff, CircleShape),
            contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(Modifier.padding(horizontal = 4.dp).size(ZoneSpec.Knob).background(Color.White, CircleShape))
        }
    }
}

/** − 20 min +, in steps of 5 within 5..60; effective at once (plan 06 decision 6). */
@Composable
private fun LimitStepper(minutes: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        StepButton("−", "Less time", enabled = minutes > MIN_LIMIT_MINUTES) { onChange(minutes - LIMIT_STEP_MINUTES) }
        Text(
            "$minutes min",
            style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold),
            color = HuroofiTokens.Navy,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = ZoneSpec.StepperValue),
        )
        StepButton("+", "More time", enabled = minutes < MAX_LIMIT_MINUTES) { onChange(minutes + LIMIT_STEP_MINUTES) }
    }
}

@Composable
private fun StepButton(symbol: String, label: String, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(ZoneSpec.StepperRadius)
    Box(
        Modifier
            .size(ZoneSpec.Stepper)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, ZoneSpec.GhostBorder, shape)
            .semantics { contentDescription = label }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            symbol,
            style = HuroofiText.buttonPrimary,
            color = HuroofiTokens.PrimaryShadow,
        )
    }
}

@Composable
private fun ProgressBar(progress: ParentProgress) {
    val total = progress.states.size.coerceAtLeast(1).toFloat()
    Row(
        Modifier
            .fillMaxWidth()
            .height(ZoneSpec.BarHeight)
            .clip(CircleShape)
            .background(ZoneSpec.BarTrack)
            .border(1.dp, ZoneSpec.ToGoOutline, CircleShape),
    ) {
        val learned = progress.learned / total
        val learning = progress.learning / total
        if (learned > 0f) Box(Modifier.fillMaxHeight().weight(learned).background(ZoneSpec.Learned))
        if (learning > 0f) Box(Modifier.fillMaxHeight().weight(learning).background(ZoneSpec.Learning))
        val rest = 1f - learned - learning
        if (rest > 0f) Spacer(Modifier.weight(rest))
    }
}

@Composable
private fun LegendItem(color: Color, label: String, ring: Color? = null) {
    val shape = RoundedCornerShape(4.dp)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier.size(ZoneSpec.Legend).background(color, shape)
                .then(if (ring != null) Modifier.border(if (ring == ZoneSpec.Learning) 3.dp else 1.dp, ring, shape) else Modifier),
        )
        Text(label, style = HuroofiText.caption, color = HuroofiTokens.Muted)
    }
}

/** 7 columns, alif top-right (plan 06 decision 3: display only). */
@Composable
private fun LetterGrid(letters: List<GridLetter>) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(verticalArrangement = Arrangement.spacedBy(ZoneSpec.CellGap)) {
            for (row in letters.chunked(ZoneSpec.GRID_COLUMNS)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ZoneSpec.CellGap)) {
                    for (letter in row) LetterCell(letter, Modifier.weight(1f))
                    repeat(ZoneSpec.GRID_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun LetterCell(letter: GridLetter, modifier: Modifier) {
    val shape = RoundedCornerShape(ZoneSpec.CellRadius)
    val (fill, glyph) = when (letter.state) {
        LetterState.LEARNED -> ZoneSpec.Learned to Color.White
        LetterState.LEARNING -> Color.White to HuroofiTokens.Navy
        LetterState.TO_GO -> ZoneSpec.ToGoCell to HuroofiTokens.Muted
    }
    val status = when (letter.state) {
        LetterState.LEARNED -> "learned"
        LetterState.LEARNING -> "learning"
        LetterState.TO_GO -> "to go"
    }
    Box(
        modifier
            .height(ZoneSpec.Cell)
            .background(fill, shape)
            .then(
                if (letter.state == LetterState.LEARNING) Modifier.border(ZoneSpec.CellRing, ZoneSpec.Learning, shape)
                else Modifier,
            )
            .semantics(mergeDescendants = true) { contentDescription = "${letter.name}, $status" },
        contentAlignment = Alignment.Center,
    ) {
        CenteredLetter(letter.glyph, size = ZoneSpec.CellGlyph, color = glyph, style = HuroofiText.arabicChip.copy(fontFamily = NotoNaskhArabic))
    }
}

@Preview(widthDp = 390, heightDp = 1000)
@Composable
private fun ParentZonePreview() {
    val stage = Stage(1, "Sunny Meadow", emptyList(), "#FFC93C", "#FFF4D6", "#8A5A00", "lion")
    val glyphs = listOf("أ", "ب", "ت", "ث", "ج", "ح", "خ")
    val letters = List(28) { i ->
        val state = when (i) {
            0, 1 -> LetterState.LEARNED
            2 -> LetterState.LEARNING
            else -> LetterState.TO_GO
        }
        GridLetter(glyphs[i % glyphs.size], "letter ${i + 1}", state)
    }
    val progress = ParentProgress(stage, 7, letters.mapIndexed { i, l -> i + 1 to l.state }.toMap())
    HuroofiTheme { ParentZoneScreen(progress, letters, AgeMode.TODDLER, onBack = {}, onModeChange = {}, voice = true, onVoiceChange = {}, harakat = false, onHarakatChange = {}, limitMinutes = 20, onLimitChange = {}, unlockAll = false, onUnlockAllChange = {}, traceOnly = false, onTraceOnlyChange = {}, onStartOver = {}, haptics = true, onHapticsChange = {}) }
}
