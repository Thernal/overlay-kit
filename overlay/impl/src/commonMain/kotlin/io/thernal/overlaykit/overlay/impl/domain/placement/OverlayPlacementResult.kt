package io.thernal.overlaykit.overlay.impl.domain.placement

import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement

data class OverlayPlacementResult(
    val x: Int,
    val y: Int,
    val placement: OverlayPlacement,
)
