package io.thernal.overlaykit.overlay.components.showcase

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme
import io.thernal.overlaykit.overlay.components.tooltip.TooltipBalloon
import io.thernal.overlaykit.overlay.core.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.core.back.OverlayBackHandler
import io.thernal.overlaykit.overlay.core.host.OverlayModalEffect

@Composable
internal fun ShowcaseHost(entry: ShowcaseEntry) {
    val anchorRegistry = LocalOverlayAnchorRegistry.current
    val latestOnDismissRequest by rememberUpdatedState(entry.onDismissRequest)
    val strings = OverlayTheme.styles.strings

    // Armed a frame after the entry arrives, so the first frame draws the scrim clear and the
    // fade starts from there.
    var isAnimatedVisible by remember(entry.anchorId) { mutableStateOf(false) }
    LaunchedEffect(key1 = entry.anchorId, key2 = entry.isVisible) {
        isAnimatedVisible = entry.isVisible
    }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (isAnimatedVisible) {
            1f
        } else {
            0f
        },
        animationSpec = tween(
            durationMillis = if (isAnimatedVisible) {
                entry.style.enterMillis
            } else {
                entry.style.exitMillis
            },
        ),
        label = "showcase_scrim_alpha",
    )

    OverlayModalEffect(owner = entry.anchorId, isActive = entry.isVisible)
    if (entry.isVisible) {
        OverlayBackHandler(onBack = { latestOnDismissRequest() })
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            // Offscreen, so the cut-out clears the dim inside this layer only.
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .then(
                if (entry.isVisible) {
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures { latestOnDismissRequest() }
                        }
                        .clearAndSetSemantics {
                            contentDescription = strings.showcasePane
                            onClick(label = strings.dismiss) {
                                latestOnDismissRequest()
                                true
                            }
                        }
                } else {
                    Modifier
                },
            ),
    ) {
        drawRect(color = entry.style.scrimColor, alpha = scrimAlpha)

        val anchorBounds = anchorRegistry.getBounds(entry.anchorId) ?: return@Canvas
        val paddingPx = entry.style.anchorPadding.toPx()
        val holeSize = Size(
            width = anchorBounds.width + paddingPx * 2,
            height = anchorBounds.height + paddingPx * 2,
        )
        val outline = entry.anchorShape.createOutline(
            size = holeSize,
            layoutDirection = layoutDirection,
            density = this,
        )
        translate(left = anchorBounds.left - paddingPx, top = anchorBounds.top - paddingPx) {
            drawOutline(outline = outline, color = Color.Transparent, blendMode = BlendMode.Clear)
        }
    }

    TooltipBalloon(
        anchorId = entry.anchorId,
        placement = entry.placement,
        style = entry.style.balloon,
        isVisible = isAnimatedVisible,
        showKey = Unit,
        content = entry.content,
    )
}
