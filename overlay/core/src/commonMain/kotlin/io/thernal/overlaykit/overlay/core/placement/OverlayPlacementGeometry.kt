package io.thernal.overlaykit.overlay.core.placement

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

/**
 * Single source of truth for anchored-overlay geometry. Both [DefaultOverlayPlacementResolver]
 * and [OverlayPositionCalculator] derive positions from here; the caret shift in the calculator
 * is only correct while both sides share the same raw-position math.
 */
object OverlayPlacementGeometry {

    fun rawPosition(
        placement: OverlayPlacement,
        anchorBounds: IntRect,
        popupContentSize: IntSize,
        spacing: Int,
    ): IntOffset {
        val anchorCenterX = anchorBounds.left + anchorBounds.width / 2
        val anchorCenterY = anchorBounds.top + anchorBounds.height / 2

        return when (placement) {
            OverlayPlacement.Top -> IntOffset(
                x = anchorCenterX - popupContentSize.width / 2,
                y = anchorBounds.top - popupContentSize.height - spacing,
            )

            OverlayPlacement.Bottom -> IntOffset(
                x = anchorCenterX - popupContentSize.width / 2,
                y = anchorBounds.bottom + spacing,
            )

            OverlayPlacement.Start -> IntOffset(
                x = anchorBounds.left - popupContentSize.width - spacing,
                y = anchorCenterY - popupContentSize.height / 2,
            )

            OverlayPlacement.End -> IntOffset(
                x = anchorBounds.right + spacing,
                y = anchorCenterY - popupContentSize.height / 2,
            )

            OverlayPlacement.TopStart -> IntOffset(
                x = anchorBounds.left - popupContentSize.width,
                y = anchorBounds.top - popupContentSize.height - spacing,
            )

            OverlayPlacement.TopEnd -> IntOffset(
                x = anchorBounds.right,
                y = anchorBounds.top - popupContentSize.height - spacing,
            )

            OverlayPlacement.BottomStart -> IntOffset(
                x = anchorBounds.left - popupContentSize.width,
                y = anchorBounds.bottom + spacing,
            )

            OverlayPlacement.BottomEnd -> IntOffset(
                x = anchorBounds.right,
                y = anchorBounds.bottom + spacing,
            )
        }
    }

    fun fits(
        position: IntOffset,
        popupContentSize: IntSize,
        windowSize: IntSize,
        edgeMargin: Int,
    ): Boolean {
        val rightLimit = windowSize.width - edgeMargin
        val bottomLimit = windowSize.height - edgeMargin

        return position.x >= edgeMargin && position.y >= edgeMargin &&
            position.x + popupContentSize.width <= rightLimit &&
            position.y + popupContentSize.height <= bottomLimit
    }

    fun clampX(
        x: Int,
        popupWidth: Int,
        windowWidth: Int,
        edgeMargin: Int,
    ): Int {
        val maxX = (windowWidth - edgeMargin - popupWidth).coerceAtLeast(edgeMargin)
        return x.coerceIn(edgeMargin, maxX)
    }

    fun clampY(
        y: Int,
        popupHeight: Int,
        windowHeight: Int,
        edgeMargin: Int,
    ): Int {
        val maxY = (windowHeight - edgeMargin - popupHeight).coerceAtLeast(edgeMargin)
        return y.coerceIn(edgeMargin, maxY)
    }
}

fun OverlayPlacement.toSemanticPlacement(layoutDirection: LayoutDirection): OverlayPlacement {
    if (layoutDirection != LayoutDirection.Rtl) {
        return this
    }
    return when (this) {
        OverlayPlacement.Start -> OverlayPlacement.End

        OverlayPlacement.End -> OverlayPlacement.Start

        OverlayPlacement.BottomStart -> OverlayPlacement.BottomEnd

        OverlayPlacement.BottomEnd -> OverlayPlacement.BottomStart

        OverlayPlacement.TopStart -> OverlayPlacement.TopEnd

        OverlayPlacement.TopEnd -> OverlayPlacement.TopStart

        OverlayPlacement.Top,
        OverlayPlacement.Bottom,
        -> this
    }
}
