package com.huroofi.app.gate

import org.junit.Assert.assertEquals
import org.junit.Test

class GateActionsTest {
    @Test
    fun noActionsUntilUnlocked() {
        assertEquals(emptyList<GateAction>(), gateActions(HoldState.Idle))
        assertEquals(emptyList<GateAction>(), gateActions(HoldState.Holding(progress = 0.9f)))
    }

    @Test
    fun unlockedOffersOpenParentZoneAndCloseAppAndNothingElse() {
        assertEquals(
            listOf(GateAction.OpenParentZone, GateAction.CloseApp),
            gateActions(HoldState.Unlocked),
        )
    }
}
