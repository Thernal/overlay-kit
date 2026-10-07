package io.thernal.overlaykit.overlay.impl.domain.placement

import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement

interface OverlayPlacementResolver {
    fun resolve(
        anchorBounds: IntRect,
        windowSize: IntSize,
        popupContentSize: IntSize,
        preferredPlacement: OverlayPlacement,
        edgeMargin: Int,
        anchorSpacing: Int,
    ): OverlayPlacementResult
}
