package com.huroofi.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Raw colour values from HANDOFF section 5. */
object HuroofiTokens {
    val Navy = Color(0xFF13294B)
    val Muted = Color(0xFF4A5D7A)
    val Sky = Color(0xFFE8F4FF)
    val Card = Color(0xFFFFFFFF)
    val Primary = Color(0xFF1F6FE0)
    val PrimaryShadow = Color(0xFF1555B0)
    val Sun = Color(0xFFFFC93C)
    val SunShadow = Color(0xFFE0A400)
    val Success = Color(0xFF158048)
    val SuccessShadow = Color(0xFF0F5F35)
    val Outline = Color(0xFF26324A)
    val ParentBg = Color(0xFFF4F7FB)
    val GateBg = Color(0xFF13294B)
}

/** Tokens that have no Material 3 slot. Read with [LocalHuroofiColors]. */
@Immutable
data class HuroofiColors(
    val navy: Color = HuroofiTokens.Navy,
    val muted: Color = HuroofiTokens.Muted,
    val sky: Color = HuroofiTokens.Sky,
    val card: Color = HuroofiTokens.Card,
    val primary: Color = HuroofiTokens.Primary,
    val primaryShadow: Color = HuroofiTokens.PrimaryShadow,
    val sun: Color = HuroofiTokens.Sun,
    val sunShadow: Color = HuroofiTokens.SunShadow,
    val success: Color = HuroofiTokens.Success,
    val successShadow: Color = HuroofiTokens.SuccessShadow,
    val outline: Color = HuroofiTokens.Outline,
    val parentBg: Color = HuroofiTokens.ParentBg,
    val gateBg: Color = HuroofiTokens.GateBg,
)

val LocalHuroofiColors = staticCompositionLocalOf { HuroofiColors() }

/** Light only: the child app has no dark theme. */
fun huroofiColorScheme(): ColorScheme = lightColorScheme(
    primary = HuroofiTokens.Primary,
    onPrimary = HuroofiTokens.Card,
    secondary = HuroofiTokens.Sun,
    onSecondary = HuroofiTokens.Navy,
    tertiary = HuroofiTokens.Success,
    onTertiary = HuroofiTokens.Card,
    background = HuroofiTokens.Sky,
    onBackground = HuroofiTokens.Navy,
    surface = HuroofiTokens.Card,
    onSurface = HuroofiTokens.Navy,
    onSurfaceVariant = HuroofiTokens.Muted,
    outline = HuroofiTokens.Outline,
)

/** Stage colour family. Data comes from letters.json (plan 03), never hard-coded. */
@Immutable
data class StageColors(val border: Color, val pastel: Color, val accent: Color) {
    companion object {
        private val hex = Regex("^#[0-9A-Fa-f]{6}$")

        private fun parse(value: String): Color {
            require(hex.matches(value)) { "Not a #RRGGBB colour: $value" }
            return Color(0xFF000000L or value.substring(1).toLong(16))
        }

        fun fromHex(border: String, pastel: String, accent: String) =
            StageColors(parse(border), parse(pastel), parse(accent))
    }
}

