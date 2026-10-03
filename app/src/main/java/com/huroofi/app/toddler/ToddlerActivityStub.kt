package com.huroofi.app.toddler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.LocalHuroofiColors

/** Temporary screen for an activity not built yet. Deleted once slices 3-5 land. */
@Composable
fun ToddlerActivityStub(activity: ToddlerActivity, onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    ToddlerScaffold {
        ToddlerHeader(onHome = onHome, onRequestParentZone = onRequestParentZone)
        Column(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArabicText(activity.arabicLabel, size = 40.sp, color = activity.tile.text)
            Text("Coming soon", style = HuroofiText.body, color = LocalHuroofiColors.current.muted)
        }
    }
}
