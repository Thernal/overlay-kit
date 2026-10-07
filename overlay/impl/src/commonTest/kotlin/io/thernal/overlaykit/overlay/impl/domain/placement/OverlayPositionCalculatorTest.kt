package io.thernal.overlaykit.overlay.impl.domain.placement

import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import kotlin.test.Test
import kotlin.test.assertEquals

class OverlayPositionCalculatorTest {

    @Test
    fun `pins the preferred placement and only clamps the cross axis when it already fits vertically`() {
        val calculator = OverlayPositionCalculator(
            anchorBounds = IntRect(left = 0, top = 200, right = 40, bottom = 220),
            preferredPlacement = OverlayPlacement.Bottom,
            placementResolver = OverlayPlacementResolverImpl(),
            edgeMarginPx = 5,
            anchorSpacingPx = 5,
        )

        val result = calculator.calculate(
            windowSize = IntSize(width = 300, height = 400),
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = IntSize(width = 100, height = 10),
        )

        assertEquals(
            OverlayCalculatedPosition(placement = OverlayPlacement.Bottom, x = 5, y = 225, caretCenterOffsetPx = -35f),
            result,
        )
    }

    @Test
    fun `falls back to the resolver result when the preferred placement cannot be pinned`() {
        val calculator = OverlayPositionCalculator(
            anchorBounds = IntRect(left = 100, top = 200, right = 140, bottom = 220),
            preferredPlacement = OverlayPlacement.Top,
            placementResolver = OverlayPlacementResolverImpl(),
            edgeMarginPx = 5,
            anchorSpacingPx = 5,
        )

        val result = calculator.calculate(
            windowSize = IntSize(width = 30, height = 30),
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = IntSize(width = 20, height = 10),
        )

        assertEquals(
            OverlayCalculatedPosition(placement = OverlayPlacement.Top, x = 5, y = 15, caretCenterOffsetPx = 105f),
            result,
        )
    }
}
