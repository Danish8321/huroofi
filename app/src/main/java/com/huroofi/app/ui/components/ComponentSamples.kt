package com.huroofi.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.huroofi.app.R
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.StageColors

/** Sample stage 1 colours from HANDOFF section 5; real values come from letters.json. */
private val sampleStage = StageColors.fromHex("#F59E0B", "#FFEDC4", "#A35400")

/** Every shared component once. Used by the previews here and by the debug gallery. */
@Composable
fun ComponentSamples(modifier: Modifier = Modifier) {
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("PrimaryButton", style = HuroofiText.sectionHeading)
        PrimaryButton("Let's go!", onClick = {})
        PrimaryButton("Next", onClick = {}, kind = ButtonKind.Success)
        PrimaryButton("Sound", onClick = {}, kind = ButtonKind.Sun)

        Text("RoundIconButton (normal, toddler)", style = HuroofiText.sectionHeading)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            RoundIconButton("Play sound", onClick = {}) { SoundIcon(color = androidx.compose.ui.graphics.Color.White) }
            RoundIconButton("Play sound", onClick = {}, toddler = true) {
                SoundIcon(color = androidx.compose.ui.graphics.Color.White, size = 44.dp)
            }
        }

        Text("LetterChip (plain, stage, selected)", style = HuroofiText.sectionHeading)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LetterChip("ب", "Letter baa")
            LetterChip("ب", "Letter baa", stage = sampleStage)
            LetterChip("ب", "Letter baa, selected", stage = sampleStage, selected = true, onClick = {})
        }

        Text("PictureTile", style = HuroofiText.sectionHeading)
        PictureTile(
            image = painterResource(R.drawable.pic_duck),
            contentDescription = "Duck",
            stage = sampleStage,
            onClick = {},
            word = "بطة",
        )

        Text("SpeechBubble", style = HuroofiText.sectionHeading)
        SpeechBubble("أين البطة؟", "Hear the question again", onSoundClick = {})

        Text("ParentLock", style = HuroofiText.sectionHeading)
        ParentLock(onRequestParentZone = {}, contentDescription = "Parents")

        Text("StepTabs", style = HuroofiText.sectionHeading)
        StepTabs(listOf("Lesson", "Trace", "Quiz"), currentIndex = 1)

        Text("ArabicText", style = HuroofiText.sectionHeading)
        ArabicText("حروفي")
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun SamplesPreview() = HuroofiTheme { ComponentSamples() }

@Preview(showBackground = true, heightDp = 1700, fontScale = 1.3f)
@Composable
private fun SamplesLargeFontPreview() = HuroofiTheme { ComponentSamples() }

@Preview(showBackground = true, widthDp = 360, name = "Arabic")
@Composable
private fun ArabicPreview() = HuroofiTheme {
    ArabicText("أين البطة؟", size = androidx.compose.ui.unit.TextUnit(38f, androidx.compose.ui.unit.TextUnitType.Sp))
}
