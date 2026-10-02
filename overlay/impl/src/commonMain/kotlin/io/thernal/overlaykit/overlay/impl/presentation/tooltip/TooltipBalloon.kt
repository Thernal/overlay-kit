package io.thernal.overlaykit.overlay.impl.presentation.tooltip

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipStyle
import io.thernal.overlaykit.overlay.impl.presentation.anchor.OverlayAnchorId
import io.thernal.overlaykit.overlay.impl.presentation.placement.AnchoredOverlay
import io.thernal.overlaykit.overlay.impl.presentation.placement.AnchoredOverlayAnimation
import io.thernal.overlaykit.overlay.impl.presentation.placement.AnchoredOverlayState
import kotlinx.coroutines.launch

/**
 * A balloon with a caret pointing at [anchorId] — the tooltip's, and the showcase's. It pops in
 * when [isVisible] turns true (springing from the caret's tip) and fades out when it turns false;
 * a new [showKey] restarts the pop. The balloon's shape follows the placement the layout resolved,
 * read in the draw phase, so a balloon flipped to the other side of its anchor redraws without
 * recomposing.
 */
@Composable
internal fun TooltipBalloon(
    anchorId: OverlayAnchorId,
    placement: OverlayPlacement,
    style: TooltipStyle,
    isVisible: Boolean,
    showKey: Any?,
    content: @Composable () -> Unit,
) {
    val layoutState = remember(anchorId) { AnchoredOverlayState() }
    val alpha = remember(key1 = anchorId, key2 = showKey) { Animatable(initialValue = 0f) }
    val scale = remember(key1 = anchorId, key2 = showKey) { Animatable(initialValue = style.initialScale) }

    LaunchedEffect(key1 = alpha, key2 = scale, key3 = isVisible) {
        if (isVisible) {
            launch {
                alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = style.enterMillis))
            }
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        } else {
            val exit = tween<Float>(durationMillis = style.exitMillis, easing = FastOutLinearInEasing)
            launch {
                alpha.animateTo(targetValue = 0f, animationSpec = exit)
            }
            scale.animateTo(targetValue = style.initialScale, animationSpec = exit)
        }
    }
    val animation = remember(key1 = alpha, key2 = scale) {
        AnchoredOverlayAnimation(
            alpha = { alpha.value },
            scale = { scale.value },
        )
    }

    AnchoredOverlay(
        anchorId = anchorId,
        preferredPlacement = placement,
        edgeMargin = style.edgeMargin,
        anchorSpacing = style.anchorSpacing,
        modifier = Modifier.fillMaxSize(),
        state = layoutState,
        animation = animation,
        caretInset = if (style.caret.isEnabled) {
            style.caret.height
        } else {
            0.dp
        },
        chrome = {
            Spacer(
                modifier = Modifier.drawBehind {
                    val position = layoutState.position ?: return@drawBehind
                    val outline = TooltipShape(
                        cornerRadiusPx = style.cornerRadius.toPx(),
                        caretWidthPx = style.caret.width.toPx(),
                        caretHeightPx = style.caret.height.toPx(),
                        caretEdge = position.placement.toCaretEdge(isEnabled = style.caret.isEnabled),
                        caretCenterOffsetPx = position.caretCenterOffsetPx,
                        sharpCorner = position.placement.toSharpCorner(),
                    ).createOutline(size = size, layoutDirection = layoutDirection, density = this)
                    drawOutline(outline = outline, color = style.containerColor)
                    if (style.borderWidth.toPx() > 0f) {
                        drawOutline(
                            outline = outline,
                            color = style.borderColor,
                            style = Stroke(width = style.borderWidth.toPx()),
                        )
                    }
                },
            )
        },
    ) {
        Box(
            modifier = Modifier
                // Taps on the balloon stay on it; the dismiss catcher behind must not see them.
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {})
                }
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                }
                .padding(style.contentPadding),
        ) {
            content()
        }
    }
}
