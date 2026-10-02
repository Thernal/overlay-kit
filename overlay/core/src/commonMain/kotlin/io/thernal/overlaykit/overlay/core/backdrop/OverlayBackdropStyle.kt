package io.thernal.overlaykit.overlay.core.backdrop

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * What a fully raised backdrop does to the content behind it: scaled to [minScale] around its top
 * centre, lifted by [maxLift], its corners rounded to [maxCornerRadius]. [None] leaves it alone.
 */
@Immutable
data class OverlayBackdropStyle(
    val minScale: Float = DEFAULT_MIN_SCALE,
    val maxLift: Dp = 8.dp,
    val maxCornerRadius: Dp = 80.dp,
) {
    companion object {
        private const val DEFAULT_MIN_SCALE = 0.965f

        val None: OverlayBackdropStyle = OverlayBackdropStyle(
            minScale = 1f,
            maxLift = 0.dp,
            maxCornerRadius = 0.dp,
        )
    }
}
