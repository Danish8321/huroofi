package com.huroofi.app.learn

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp

/** The prototypes' solid bottom edge (`box-shadow: 0 Ndp 0 colour`): the shape again, [depth] lower, behind. */
fun Modifier.dropEdge(color: Color, depth: Dp, shape: Shape): Modifier = drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(top = depth.toPx()) { drawOutline(outline, color) }
}

/** The three steps of the Letter path (glossary). [tab] labels the step tabs, [tile] the Home tiles. */
enum class PathStep(val tab: String, val tile: String) {
    MEET("Meet", "Learn"),
    TRACE("Trace", "Trace"),
    PLAY("Play", "Play"),
}

/** Prototype SVG paths (24-unit box) for the learn screens' outline icons. */
object LearnIcons {
    val Book = listOf("M4 5a2 2 0 0 1 2-2h13v16H6a2 2 0 0 0-2 2z", "M4 19V5", "M9 8h6")
    val Pencil = listOf("M4 20l4-1 11-11-3-3L5 16z", "M14 6l3 3")
    val Gamepad = listOf(
        "M8 7h8a5 5 0 0 1 5 5v1a5 5 0 0 1-5 5H8a5 5 0 0 1-5-5v-1a5 5 0 0 1 5-5z",
        "M8 10v5M5.5 12.5h5",
        "M16 11h.01M18 14h.01",
    )
    val Home = listOf("M3 11l9-7 9 7", "M5 10v10h14V10")
    val Map = listOf("M9 4L3 6v14l6-2 6 2 6-2V4l-6 2-6-2z", "M9 4v14M15 6v14")
    val Star = listOf("M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z")
    val Lock = listOf("M7 11h10a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2z", "M8 11V8a4 4 0 0 1 8 0v3")
    val Next = listOf("M9 5l7 7-7 7")
    val Check = listOf("M5 12l5 5 9-10")

    fun forStep(step: PathStep): List<String> = when (step) {
        PathStep.MEET -> Book
        PathStep.TRACE -> Pencil
        PathStep.PLAY -> Gamepad
    }
}
