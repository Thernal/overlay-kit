package io.thernal.overlaykit.overlay.components.sheet

import kotlin.test.Test
import kotlin.test.assertEquals

private const val HEIGHT = 1_000f
private const val MIN_FLING = 300f
private const val THRESHOLD = 0.35f

class SheetSettleTargetTest {

    private fun target(
        offset: Float,
        velocity: Float,
    ): SheetValue {
        return sheetSettleTarget(
            offsetPx = offset,
            heightPx = HEIGHT,
            velocityPx = velocity,
            minFlingVelocityPx = MIN_FLING,
            dismissThreshold = THRESHOLD,
        )
    }

    @Test
    fun `a fast fling down closes the sheet however little it moved`() {
        assertEquals(SheetValue.Hidden, target(offset = 10f, velocity = 1_000f))
    }

    @Test
    fun `a fast fling up keeps the sheet open however far it was pulled`() {
        assertEquals(SheetValue.Expanded, target(offset = 900f, velocity = -1_000f))
    }

    @Test
    fun `a slow release past the threshold closes the sheet`() {
        assertEquals(SheetValue.Hidden, target(offset = 400f, velocity = 0f))
    }

    @Test
    fun `a slow release short of the threshold keeps the sheet open`() {
        assertEquals(SheetValue.Expanded, target(offset = 300f, velocity = 100f))
    }
}
