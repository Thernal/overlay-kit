package io.thernal.overlaykit.overlay.impl.presentation.placement

import androidx.compose.runtime.Stable

/** The enter or exit animation applied to the whole popup, around the point touching the anchor. */
@Stable
class AnchoredOverlayAnimation(
    val alpha: () -> Float,
    val scale: () -> Float,
) {
    companion object {
        val None: AnchoredOverlayAnimation = AnchoredOverlayAnimation(
            alpha = { 1f },
            scale = { 1f },
        )
    }
}
