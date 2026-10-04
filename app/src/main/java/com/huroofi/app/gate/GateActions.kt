package com.huroofi.app.gate

enum class GateAction { OpenParentZone, CloseApp }

/** Both actions sit behind the same hold. "Back to play" is not one of them: it shows before the hold too. */
fun gateActions(state: HoldState): List<GateAction> =
    if (state == HoldState.Unlocked) listOf(GateAction.OpenParentZone, GateAction.CloseApp) else emptyList()
