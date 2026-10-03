package com.huroofi.app.gate

enum class GateAction { OpenParentZone, CloseApp }

/** Both actions sit behind the same hold. There is deliberately no cancel action (Back goes home). */
fun gateActions(state: HoldState): List<GateAction> =
    if (state == HoldState.Unlocked) listOf(GateAction.OpenParentZone, GateAction.CloseApp) else emptyList()
