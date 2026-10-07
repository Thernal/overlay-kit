package io.thernal.overlaykit.overlay.impl.domain.placement

import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import kotlin.test.Test
import kotlin.test.assertEquals

class OverlayPlacementResolverImplTest {

    private val resolver = OverlayPlacementResolverImpl()

    @Test
    fun `keeps the preferred placement when it already fits`() {
        val result = resolver.resolve(
            anchorBounds = IntRect(left = 100, top = 200, right = 140, bottom = 220),
            windowSize = IntSize(width = 300, height = 400),
            popupContentSize = IntSize(width = 20, height = 10),
            preferredPlacement = OverlayPlacement.Top,
            edgeMargin = 5,
            anchorSpacing = 5,
        )

        assertEquals(OverlayPlacementResult(x = 110, y = 185, placement = OverlayPlacement.Top), result)
    }

    @Test
    fun `falls through to the next candidate when the preferred placement does not fit`() {
        val result = resolver.resolve(
            anchorBounds = IntRect(left = 100, top = 0, right = 140, bottom = 20),
            windowSize = IntSize(width = 300, height = 400),
            popupContentSize = IntSize(width = 20, height = 10),
            preferredPlacement = OverlayPlacement.Top,
            edgeMargin = 5,
            anchorSpacing = 5,
        )

        assertEquals(OverlayPlacementResult(x = 110, y = 25, placement = OverlayPlacement.Bottom), result)
    }

    @Test
    fun `clamps the preferred placement as a last resort when no candidate fits`() {
        val result = resolver.resolve(
            anchorBounds = IntRect(left = 100, top = 200, right = 140, bottom = 220),
            windowSize = IntSize(width = 30, height = 30),
            popupContentSize = IntSize(width = 20, height = 10),
            preferredPlacement = OverlayPlacement.Top,
            edgeMargin = 5,
            anchorSpacing = 5,
        )

        assertEquals(OverlayPlacementResult(x = 5, y = 15, placement = OverlayPlacement.Top), result)
    }
}
