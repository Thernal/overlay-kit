package io.thernal.overlaykit.overlay.core.backdrop

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

private val BACKDROP_TRANSFORM_ORIGIN = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0f)

/**
 * Pushes the content back by [progress] as [style] describes. Everything is read inside the layer
 * block, so an animating progress redraws the layer without recomposing or relaying out the content.
 */
internal fun Modifier.backdropTransform(
    progress: State<Float>,
    style: OverlayBackdropStyle,
): Modifier {
    if (style == OverlayBackdropStyle.None) {
        return this
    }
    return graphicsLayer {
        val currentProgress = progress.value
        val scale = 1f - (1f - style.minScale) * currentProgress
        scaleX = scale
        scaleY = scale
        translationY = -style.maxLift.toPx() * currentProgress
        shape = RoundedCornerShape(
            lerp(
                start = 0.dp,
                stop = style.maxCornerRadius,
                fraction = currentProgress,
            ),
        )
        clip = currentProgress > 0f
        transformOrigin = BACKDROP_TRANSFORM_ORIGIN
    }
}
