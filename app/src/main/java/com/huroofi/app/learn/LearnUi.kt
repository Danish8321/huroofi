package com.huroofi.app.learn

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import com.huroofi.app.ui.components.StepTab

/** The prototypes' solid bottom edge (`box-shadow: 0 Ndp 0 colour`): the shape again, [depth] lower, behind. */
fun Modifier.dropEdge(color: Color, depth: Dp, shape: Shape): Modifier = drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(top = depth.toPx()) { drawOutline(outline, color) }
}

/** The three steps of the Letter path (glossary). [tab] is its mark in the step bar, icons from the prototype. */
enum class PathStep(val tab: StepTab) {
    MEET(StepTab("Meet", listOf("M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z", "M9 12a3 3 0 1 0 6 0a3 3 0 1 0 -6 0"))),
    TRACE(StepTab("Trace", listOf("M4 20l4-1 11-11-3-3L5 16z", "M14 6l3 3"))),
    PLAY(StepTab("Play", listOf("M8 5.5v13l10.5-6.5z"), filled = true)),
}

/** Prototype SVG paths (24-unit box) for the learn screens' outline icons. */
object LearnIcons {
    val Home = listOf("M3 11l9-7 9 7", "M5 10v10h14V10")
    val Map = listOf("M9 4L3 6v14l6-2 6 2 6-2V4l-6 2-6-2z", "M9 4v14M15 6v14")
    val Star = listOf("M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z")
    val Lock = listOf("M7 11h10a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2z", "M8 11V8a4 4 0 0 1 8 0v3")
    val Next = listOf("M9 5l7 7-7 7")
    val Check = listOf("M5 12l5 5 9-10")
}
