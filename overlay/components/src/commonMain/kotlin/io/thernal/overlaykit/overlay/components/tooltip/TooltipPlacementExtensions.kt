package io.thernal.overlaykit.overlay.components.tooltip

import io.thernal.overlaykit.overlay.core.placement.OverlayPlacement

internal fun OverlayPlacement.toCaretEdge(isEnabled: Boolean): OverlayPlacement? {
    if (!isEnabled) {
        return null
    }

    return when (this) {
        OverlayPlacement.Top -> OverlayPlacement.Bottom

        OverlayPlacement.Bottom -> OverlayPlacement.Top

        OverlayPlacement.Start -> OverlayPlacement.End

        OverlayPlacement.End -> OverlayPlacement.Start

        OverlayPlacement.TopStart,
        OverlayPlacement.TopEnd,
        OverlayPlacement.BottomStart,
        OverlayPlacement.BottomEnd,
        -> null
    }
}

internal fun OverlayPlacement.toSharpCorner(): TooltipSharpCorner? {
    return when (this) {
        OverlayPlacement.BottomStart -> TooltipSharpCorner.TopEnd

        OverlayPlacement.BottomEnd -> TooltipSharpCorner.TopStart

        OverlayPlacement.TopStart -> TooltipSharpCorner.BottomEnd

        OverlayPlacement.TopEnd -> TooltipSharpCorner.BottomStart

        OverlayPlacement.Top,
        OverlayPlacement.Bottom,
        OverlayPlacement.Start,
        OverlayPlacement.End,
        -> null
    }
}
