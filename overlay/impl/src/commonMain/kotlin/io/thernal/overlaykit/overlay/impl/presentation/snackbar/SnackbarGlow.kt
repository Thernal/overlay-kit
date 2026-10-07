package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarPosition
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarGlowStyle

/** The wash of [tone] along the edge at [position], fading with the message. */
@Composable
internal fun BoxScope.SnackbarGlow(
    tone: Color,
    position: SnackbarPosition,
    isVisible: Boolean,
    style: SnackbarGlowStyle,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = style.animationMillis, easing = GlowEasing)),
        exit = fadeOut(animationSpec = tween(durationMillis = style.animationMillis, easing = GlowEasing)),
        modifier = Modifier.matchParentSize(),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val glowHeight = size.height * style.heightFraction
            val (startY, endY) = when (position) {
                SnackbarPosition.Top -> 0f to glowHeight
                SnackbarPosition.Bottom -> size.height to size.height - glowHeight
            }
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        tone.copy(alpha = style.edgeAlpha),
                        tone.copy(alpha = style.midAlpha),
                        Color.Transparent,
                    ),
                    startY = startY,
                    endY = endY,
                ),
            )
        }
    }
}

private val GlowEasing = CubicBezierEasing(a = 0.4f, b = 0f, c = 0.2f, d = 1f)
