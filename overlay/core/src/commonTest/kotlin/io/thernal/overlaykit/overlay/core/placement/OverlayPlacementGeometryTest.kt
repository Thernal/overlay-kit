package io.thernal.overlaykit.overlay.core.placement

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val anchorBounds = IntRect(left = 100, top = 200, right = 140, bottom = 220)
private val popupContentSize = IntSize(width = 20, height = 10)
private const val SPACING = 5

class OverlayPlacementGeometryTest {

    @Test
    fun `rawPosition centers the popup on the anchor for edge placements`() {
        assertEquals(
            IntOffset(x = 110, y = 185),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.Top, anchorBounds, popupContentSize, SPACING),
        )
        assertEquals(
            IntOffset(x = 110, y = 225),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.Bottom, anchorBounds, popupContentSize, SPACING),
        )
        assertEquals(
            IntOffset(x = 75, y = 205),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.Start, anchorBounds, popupContentSize, SPACING),
        )
        assertEquals(
            IntOffset(x = 145, y = 205),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.End, anchorBounds, popupContentSize, SPACING),
        )
    }

    @Test
    fun `rawPosition aligns corner placements to the anchor edge instead of its center`() {
        assertEquals(
            IntOffset(x = 80, y = 185),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.TopStart, anchorBounds, popupContentSize, SPACING),
        )
        assertEquals(
            IntOffset(x = 140, y = 185),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.TopEnd, anchorBounds, popupContentSize, SPACING),
        )
        assertEquals(
            IntOffset(x = 80, y = 225),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.BottomStart, anchorBounds, popupContentSize, SPACING),
        )
        assertEquals(
            IntOffset(x = 140, y = 225),
            OverlayPlacementGeometry.rawPosition(OverlayPlacement.BottomEnd, anchorBounds, popupContentSize, SPACING),
        )
    }

    @Test
    fun `fits is true only when the popup stays within the margins on every side`() {
        val windowSize = IntSize(width = 300, height = 400)

        assertTrue(
            OverlayPlacementGeometry.fits(
                position = IntOffset(10, 10),
                popupContentSize = popupContentSize,
                windowSize = windowSize,
                edgeMargin = 5,
            ),
        )
        assertFalse(
            OverlayPlacementGeometry.fits(
                position = IntOffset(-1, 10),
                popupContentSize = popupContentSize,
                windowSize = windowSize,
                edgeMargin = 5,
            ),
        )
        assertFalse(
            OverlayPlacementGeometry.fits(
                position = IntOffset(290, 10),
                popupContentSize = popupContentSize,
                windowSize = windowSize,
                edgeMargin = 5,
            ),
        )
    }

    @Test
    fun `clampX keeps values already inside the window unchanged`() {
        val result = OverlayPlacementGeometry.clampX(x = 50, popupWidth = 20, windowWidth = 300, edgeMargin = 5)

        assertEquals(50, result)
    }

    @Test
    fun `clampX pulls values back inside the margins on both sides`() {
        assertEquals(5, OverlayPlacementGeometry.clampX(x = -30, popupWidth = 20, windowWidth = 300, edgeMargin = 5))
        assertEquals(
            275,
            OverlayPlacementGeometry.clampX(x = 1000, popupWidth = 20, windowWidth = 300, edgeMargin = 5),
        )
    }

    @Test
    fun `clampX collapses to the edge margin when the popup is wider than the window allows`() {
        val result = OverlayPlacementGeometry.clampX(x = 110, popupWidth = 20, windowWidth = 30, edgeMargin = 5)

        assertEquals(5, result)
    }

    @Test
    fun `clampY mirrors clampX behavior on the vertical axis`() {
        assertEquals(15, OverlayPlacementGeometry.clampY(y = 185, popupHeight = 10, windowHeight = 30, edgeMargin = 5))
    }

    @Test
    fun `toSemanticPlacement mirrors directional placements only under RTL`() {
        assertEquals(OverlayPlacement.Start, OverlayPlacement.Start.toSemanticPlacement(LayoutDirection.Ltr))
        assertEquals(OverlayPlacement.End, OverlayPlacement.Start.toSemanticPlacement(LayoutDirection.Rtl))
        assertEquals(OverlayPlacement.Start, OverlayPlacement.End.toSemanticPlacement(LayoutDirection.Rtl))
        assertEquals(
            OverlayPlacement.BottomEnd,
            OverlayPlacement.BottomStart.toSemanticPlacement(LayoutDirection.Rtl),
        )
        assertEquals(OverlayPlacement.Top, OverlayPlacement.Top.toSemanticPlacement(LayoutDirection.Rtl))
    }
}
