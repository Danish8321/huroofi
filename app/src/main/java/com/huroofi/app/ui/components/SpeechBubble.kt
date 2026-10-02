package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.TypeScale

/** Spoken prompt shown as an Arabic sentence, with the sun-coloured replay button. */
@Composable
fun SpeechBubble(
    sentence: String,
    soundContentDescription: String,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier,
    sentenceSizeSp: Float = TypeScale.ToddlerSentence.default,
) {
    val colors = LocalHuroofiColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(HuroofiDimens.TileCornerMax))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RoundIconButton(
            contentDescription = soundContentDescription,
            onClick = onSoundClick,
            toddler = true,
            containerColor = colors.sun,
        ) { SoundIcon(color = colors.navy, size = 44.dp) }
        ArabicText(
            sentence,
            modifier = Modifier.weight(1f),
            size = sentenceSizeSp.sp,
            color = colors.navy,
        )
    }
}
