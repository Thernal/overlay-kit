package io.thernal.overlaykit.overlay.core.placement

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

/**
 * Where an anchored overlay of a given size goes. The preferred placement is kept whenever it fits
 * on its own axis — only the cross axis is clamped — and the [placementResolver] picks another one
 * only when it does not. The caret offset says how far the overlay was shifted from centring on the
 * anchor, so a caret drawn at the overlay's centre plus that offset still points at the anchor.
 */
class OverlayPositionCalculator(
    private val anchorBounds: IntRect,
    private val preferredPlacement: OverlayPlacement,
    private val placementResolver: OverlayPlacementResolver,
    private val edgeMarginPx: Int,
    private val anchorSpacingPx: Int,
) {
    fun calculate(
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): OverlayCalculatedPosition {
        val semanticPlacement = preferredPlacement.toSemanticPlacement(layoutDirection)
        val resolved = placementResolver.resolve(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            popupContentSize = popupContentSize,
            preferredPlacement = semanticPlacement,
            edgeMargin = edgeMarginPx,
            anchorSpacing = anchorSpacingPx,
        )
        val pinned = resolvePinnedPreferredPosition(
            placement = semanticPlacement,
            windowSize = windowSize,
            popupContentSize = popupContentSize,
        )

        val finalPlacement = pinned?.first ?: resolved.placement
        val finalX = pinned?.second?.x ?: resolved.x
        val finalY = pinned?.second?.y ?: resolved.y

        val raw = OverlayPlacementGeometry.rawPosition(
            placement = finalPlacement,
            anchorBounds = anchorBounds,
            popupContentSize = popupContentSize,
            spacing = anchorSpacingPx,
        )

        return OverlayCalculatedPosition(
            placement = finalPlacement,
            x = finalX,
            y = finalY,
            caretCenterOffsetPx = caretCenterOffset(
                placement = finalPlacement,
                shiftX = finalX - raw.x,
                shiftY = finalY - raw.y,
            ),
        )
    }

    private fun caretCenterOffset(
        placement: OverlayPlacement,
        shiftX: Int,
        shiftY: Int,
    ): Float {
        return when (placement) {
            OverlayPlacement.Top,
            OverlayPlacement.Bottom,
            -> -shiftX.toFloat()

            OverlayPlacement.Start,
            OverlayPlacement.End,
            -> -shiftY.toFloat()

            OverlayPlacement.TopStart,
            OverlayPlacement.TopEnd,
            OverlayPlacement.BottomStart,
            OverlayPlacement.BottomEnd,
            -> 0f
        }
    }

    /**
     * Pins the preferred placement whenever it fits on its primary axis, clamping only the
     * cross axis, so the overlay does not jump to a resolver candidate merely because the
     * cross axis needed a nudge.
     */
    private fun resolvePinnedPreferredPosition(
        placement: OverlayPlacement,
        windowSize: IntSize,
        popupContentSize: IntSize,
    ): Pair<OverlayPlacement, IntOffset>? {
        val raw = OverlayPlacementGeometry.rawPosition(
            placement = placement,
            anchorBounds = anchorBounds,
            popupContentSize = popupContentSize,
            spacing = anchorSpacingPx,
        )

        return when (placement) {
            OverlayPlacement.Top,
            OverlayPlacement.Bottom,
            OverlayPlacement.TopStart,
            OverlayPlacement.TopEnd,
            OverlayPlacement.BottomStart,
            OverlayPlacement.BottomEnd,
            -> {
                val doesVerticalFit =
                    raw.y >= edgeMarginPx && raw.y + popupContentSize.height <= windowSize.height - edgeMarginPx
                if (!doesVerticalFit) {
                    null
                } else {
                    val clampedX = OverlayPlacementGeometry.clampX(
                        x = raw.x,
                        popupWidth = popupContentSize.width,
                        windowWidth = windowSize.width,
                        edgeMargin = edgeMarginPx,
                    )
                    placement to IntOffset(x = clampedX, y = raw.y)
                }
            }

            OverlayPlacement.Start,
            OverlayPlacement.End,
            -> {
                val doesHorizontalFit =
                    raw.x >= edgeMarginPx && raw.x + popupContentSize.width <= windowSize.width - edgeMarginPx
                if (!doesHorizontalFit) {
                    null
                } else {
                    val clampedY = OverlayPlacementGeometry.clampY(
                        y = raw.y,
                        popupHeight = popupContentSize.height,
                        windowHeight = windowSize.height,
                        edgeMargin = edgeMarginPx,
                    )
                    placement to IntOffset(x = raw.x, y = clampedY)
                }
            }
        }
    }
}
