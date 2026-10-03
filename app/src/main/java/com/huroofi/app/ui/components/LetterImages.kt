package com.huroofi.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import com.huroofi.app.data.content.Letter

/**
 * The letter's picture illustration. Names come from letters.json; `ContentContractTest` proves each
 * file exists. Looked up by name, so enabling resource shrinking needs a keep rule for `pic_*`.
 */
@Composable
fun letterPicture(letter: Letter): Painter {
    val resources = LocalResources.current
    val packageName = LocalContext.current.packageName
    val name = letter.pictureResourceName()
    val id = remember(name) { resources.getIdentifier(name, "drawable", packageName) }
    check(id != 0) { "No drawable named $name" }
    return painterResource(id)
}
