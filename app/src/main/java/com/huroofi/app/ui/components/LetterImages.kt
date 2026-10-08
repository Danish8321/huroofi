package com.huroofi.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage

/**
 * The letter's picture illustration. Names come from letters.json; `ContentContractTest` proves each
 * file exists. Looked up by name, so resource shrinking keeps `pic_*` through `res/raw/keep.xml`.
 */
@Composable
fun letterPicture(letter: Letter): Painter = pictureNamed(letter.pictureResourceName())

/** The stage's own picture (its sticker and Map card), also from letters.json and proved by the same test. */
@Composable
fun stagePicture(stage: Stage): Painter = pictureNamed(stage.pictureResourceName())

@Composable
private fun pictureNamed(name: String): Painter {
    val resources = LocalResources.current
    val packageName = LocalContext.current.packageName
    val id = remember(name) { resources.getIdentifier(name, "drawable", packageName) }
    check(id != 0) { "No drawable named $name" }
    return painterResource(id)
}
