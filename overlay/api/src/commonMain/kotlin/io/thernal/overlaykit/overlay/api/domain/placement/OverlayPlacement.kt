package io.thernal.overlaykit.overlay.api.domain.placement

/** Where an anchored overlay goes relative to its anchor. `Start` and `End` follow the layout direction. */
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
