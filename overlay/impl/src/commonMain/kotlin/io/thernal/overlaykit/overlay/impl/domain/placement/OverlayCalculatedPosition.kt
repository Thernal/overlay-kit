package io.thernal.overlaykit.overlay.impl.domain.placement

import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement

/** [OverlayPositionCalculator]'s answer: the placement used, the top-left corner, the caret shift. */
data class OverlayCalculatedPosition(
    val placement: OverlayPlacement,
    val x: Int,
    val y: Int,
    val caretCenterOffsetPx: Float,
)
