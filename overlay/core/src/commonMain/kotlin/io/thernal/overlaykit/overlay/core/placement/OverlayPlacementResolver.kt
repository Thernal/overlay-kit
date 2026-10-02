package io.thernal.overlaykit.overlay.core.placement

import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

enum class OverlayPlacement {
    Top,
    Bottom,
    Start,
    End,
    TopStart,
    TopEnd,
    BottomStart,
    BottomEnd,
}

data class OverlayPlacementResult(
    val x: Int,
    val y: Int,
    val placement: OverlayPlacement,
)

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

class DefaultOverlayPlacementResolver : OverlayPlacementResolver {
    override fun resolve(
        anchorBounds: IntRect,
        windowSize: IntSize,
        popupContentSize: IntSize,
        preferredPlacement: OverlayPlacement,
        edgeMargin: Int,
        anchorSpacing: Int,
    ): OverlayPlacementResult {
        val candidates = candidateOrder(preferredPlacement)

        candidates.forEach { placement ->
            val position = OverlayPlacementGeometry.rawPosition(
                placement = placement,
                anchorBounds = anchorBounds,
                popupContentSize = popupContentSize,
                spacing = anchorSpacing,
            )

            if (OverlayPlacementGeometry.fits(
                    position = position,
                    popupContentSize = popupContentSize,
                    windowSize = windowSize,
                    edgeMargin = edgeMargin,
                )
            ) {
                return OverlayPlacementResult(x = position.x, y = position.y, placement = placement)
            }
        }

        val fallback = OverlayPlacementGeometry.rawPosition(
            placement = preferredPlacement,
            anchorBounds = anchorBounds,
            popupContentSize = popupContentSize,
            spacing = anchorSpacing,
        )

        return OverlayPlacementResult(
            x = OverlayPlacementGeometry.clampX(
                x = fallback.x,
                popupWidth = popupContentSize.width,
                windowWidth = windowSize.width,
                edgeMargin = edgeMargin,
            ),
            y = OverlayPlacementGeometry.clampY(
                y = fallback.y,
                popupHeight = popupContentSize.height,
                windowHeight = windowSize.height,
                edgeMargin = edgeMargin,
            ),
            placement = preferredPlacement,
        )
    }

    private fun candidateOrder(preferred: OverlayPlacement): List<OverlayPlacement> {
        return when (preferred) {
            OverlayPlacement.Top -> listOf(
                OverlayPlacement.Top,
                OverlayPlacement.Bottom,
                OverlayPlacement.End,
                OverlayPlacement.Start,
            )

            OverlayPlacement.Bottom -> listOf(
                OverlayPlacement.Bottom,
                OverlayPlacement.Top,
                OverlayPlacement.End,
                OverlayPlacement.Start,
            )

            OverlayPlacement.Start -> listOf(
                OverlayPlacement.Start,
                OverlayPlacement.End,
                OverlayPlacement.Bottom,
                OverlayPlacement.Top,
            )

            OverlayPlacement.End -> listOf(
                OverlayPlacement.End,
                OverlayPlacement.Start,
                OverlayPlacement.Bottom,
                OverlayPlacement.Top,
            )

            OverlayPlacement.BottomStart -> listOf(
                OverlayPlacement.BottomStart,
                OverlayPlacement.TopStart,
                OverlayPlacement.BottomEnd,
                OverlayPlacement.TopEnd,
                OverlayPlacement.Bottom,
                OverlayPlacement.Top,
            )

            OverlayPlacement.BottomEnd -> listOf(
                OverlayPlacement.BottomEnd,
                OverlayPlacement.TopEnd,
                OverlayPlacement.BottomStart,
                OverlayPlacement.TopStart,
                OverlayPlacement.Bottom,
                OverlayPlacement.Top,
            )

            OverlayPlacement.TopStart -> listOf(
                OverlayPlacement.TopStart,
                OverlayPlacement.BottomStart,
                OverlayPlacement.TopEnd,
                OverlayPlacement.BottomEnd,
                OverlayPlacement.Top,
                OverlayPlacement.Bottom,
            )

            OverlayPlacement.TopEnd -> listOf(
                OverlayPlacement.TopEnd,
                OverlayPlacement.BottomEnd,
                OverlayPlacement.TopStart,
                OverlayPlacement.BottomStart,
                OverlayPlacement.Top,
                OverlayPlacement.Bottom,
            )
        }
    }
}
