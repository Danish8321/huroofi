package com.huroofi.app.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTokens

/** Bottom nav items (plan 07 decision 7). Parents is not a tab: it opens the Parent gate. */
enum class NavTab(val label: String, val icon: List<String>) {
    HOME("Home", LearnIcons.Home),
    MAP("Map", LearnIcons.Map),
    STICKERS("Stickers", LearnIcons.Star),
    PARENTS("Parents", LearnIcons.Lock),
}

object NavSpec {
    val Item = 64.dp
    val Corner = 28.dp
    val Active = HuroofiTokens.Primary
    val Idle = HuroofiTokens.Muted
    val colors = listOf(HuroofiTokens.Card, Active, Idle)

    /** Labels are 16 sp, so normal text. */
    val textPairs = listOf(
        ContrastPair(Active, HuroofiTokens.Card, large = false, "current tab label"),
        ContrastPair(Active, HuroofiTokens.Card, large = true, "current tab icon"),
        ContrastPair(Idle, HuroofiTokens.Card, large = false, "other tab label"),
        ContrastPair(Idle, HuroofiTokens.Card, large = true, "other tab icon"),
    )
}

@Composable
fun LearnNav(current: NavTab, tabs: List<NavTab>, onTab: (NavTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(HuroofiTokens.Card, RoundedCornerShape(topStart = NavSpec.Corner, topEnd = NavSpec.Corner))
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        for (tab in tabs) {
            val active = tab == current
            val color: Color = if (active) NavSpec.Active else NavSpec.Idle
            Column(
                Modifier
                    .defaultMinSize(NavSpec.Item, NavSpec.Item)
                    .semantics { selected = active }
                    .clickable(role = Role.Tab) { onTab(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LineIcon(tab.icon, color)
                Text(
                    tab.label,
                    style = HuroofiText.caption.copy(fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold),
                    color = color,
                )
            }
        }
    }
}
