package io.thernal.overlaykit.overlay.impl.presentation.placement

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.impl.domain.placement.DefaultOverlayPlacementResolver
import io.thernal.overlaykit.overlay.impl.domain.placement.OverlayCalculatedPosition
import io.thernal.overlaykit.overlay.impl.domain.placement.OverlayPlacementResolver
import io.thernal.overlaykit.overlay.impl.domain.placement.OverlayPositionCalculator
import io.thernal.overlaykit.overlay.impl.domain.placement.toSemanticPlacement
import io.thernal.overlaykit.overlay.impl.presentation.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.impl.presentation.anchor.OverlayAnchorId

/**
 * What [AnchoredOverlay] resolved on its last layout — read it in draw or layer blocks (a caret
 * shape, a transform origin), never in composition: it is written during layout.
 */
@Stable
class AnchoredOverlayState {
    var position: OverlayCalculatedPosition? by mutableStateOf(null)
        internal set

    var popupSize: IntSize by mutableStateOf(IntSize.Zero)
        internal set
}

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

/**
 * Lays [content] out next to the anchor [anchorId], in one measure-and-place pass: the content is
 * measured, the position is resolved from its real size, and both are placed in the same frame —
 * no invisible first frame, no size written to state and read back a frame later.
 *
 * [chrome] is drawn under the content at the popup's full size, which includes [caretInset] on
 * the side facing the anchor; a tooltip's balloon is a chrome. The layout fills its parent and has
 * no pointer input of its own, so touches outside the popup reach whatever is below it.
 *
 * Use it inside an overlay plugin's `Render`, where its parent is the host's full-size box.
 */
@Composable
fun AnchoredOverlay(
    anchorId: OverlayAnchorId,
    preferredPlacement: OverlayPlacement,
    edgeMargin: Dp,
    anchorSpacing: Dp,
    modifier: Modifier = Modifier,
    state: AnchoredOverlayState = remember { AnchoredOverlayState() },
    animation: AnchoredOverlayAnimation = AnchoredOverlayAnimation.None,
    caretInset: Dp = 0.dp,
    isWidthMatchingAnchor: Boolean = false,
    isAvoidingIme: Boolean = true,
    placementResolver: OverlayPlacementResolver = DefaultOverlayPlacementResolver(),
    chrome: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val registry = LocalOverlayAnchorRegistry.current
    val layoutDirection = LocalLayoutDirection.current
    val imeInsets = WindowInsets.ime
    val semanticPlacement = preferredPlacement.toSemanticPlacement(layoutDirection)

    Layout(
        modifier = modifier,
        contents = listOf(chrome, content),
    ) { (chromeMeasurables, contentMeasurables), constraints ->
        val containerWidth = constraints.maxWidth
        val containerHeight = constraints.maxHeight
        val anchorBounds = registry.getBounds(anchorId)
        if (anchorBounds == null) {
            state.position = null
            return@Layout layout(width = containerWidth, height = containerHeight) {}
        }

        val visibleHeight = if (isAvoidingIme) {
            (containerHeight - imeInsets.getBottom(this)).coerceAtLeast(0)
        } else {
            containerHeight
        }
        val metrics = PopupMetrics(
            anchorBounds = anchorBounds,
            windowSize = IntSize(width = containerWidth, height = visibleHeight),
            edgeMarginPx = edgeMargin.roundToPx(),
            anchorSpacingPx = anchorSpacing.roundToPx(),
            caretInsetPx = caretInset.roundToPx(),
        )
        val body = measureBody(
            measurables = contentMeasurables,
            metrics = metrics,
            isWidthMatchingAnchor = isWidthMatchingAnchor,
        )
        val bodySize = IntSize(
            width = body.maxOfOrNull { placeable -> placeable.width } ?: 0,
            height = body.maxOfOrNull { placeable -> placeable.height } ?: 0,
        )
        val position = resolvePosition(
            metrics = metrics,
            preferredPlacement = semanticPlacement,
            placementResolver = placementResolver,
            bodySize = bodySize,
        )
        val popupSize = popupSizeFor(
            placement = position.placement,
            bodySize = bodySize,
            caretInsetPx = metrics.caretInsetPx,
        )
        val chromePlaceables = chromeMeasurables.map { measurable ->
            measurable.measure(Constraints.fixed(width = popupSize.width, height = popupSize.height))
        }
        state.position = position
        state.popupSize = popupSize

        val bodyOffset = bodyOffsetFor(placement = position.placement, caretInsetPx = metrics.caretInsetPx)
        val pivot = anchorContactPoint(position = position, popupSize = popupSize)

        layout(width = containerWidth, height = containerHeight) {
            chromePlaceables.forEach { placeable ->
                placeable.placeWithLayer(x = position.x, y = position.y) {
                    applyAnimation(animation = animation, pivot = pivot, size = popupSize)
                }
            }
            body.forEach { placeable ->
                placeable.placeWithLayer(x = position.x + bodyOffset.x, y = position.y + bodyOffset.y) {
                    applyAnimation(
                        animation = animation,
                        pivot = pivot - Offset(x = bodyOffset.x.toFloat(), y = bodyOffset.y.toFloat()),
                        size = IntSize(width = placeable.width, height = placeable.height),
                    )
                }
            }
        }
    }
}

private class PopupMetrics(
    val anchorBounds: IntRect,
    val windowSize: IntSize,
    val edgeMarginPx: Int,
    val anchorSpacingPx: Int,
    val caretInsetPx: Int,
)

private fun MeasureScope.measureBody(
    measurables: List<Measurable>,
    metrics: PopupMetrics,
    isWidthMatchingAnchor: Boolean,
): List<Placeable> {
    val maxWidth = (metrics.windowSize.width - metrics.edgeMarginPx * 2).coerceAtLeast(0)
    val maxHeight = (metrics.windowSize.height - metrics.edgeMarginPx * 2).coerceAtLeast(0)
    val bodyConstraints = if (isWidthMatchingAnchor) {
        val width = metrics.anchorBounds.width.coerceAtMost(maxWidth)
        Constraints(minWidth = width, maxWidth = width, maxHeight = maxHeight)
    } else {
        Constraints(maxWidth = maxWidth, maxHeight = maxHeight)
    }
    return measurables.map { measurable ->
        measurable.measure(bodyConstraints)
    }
}

// The caret adds to the popup's size along the axis it points on, so the size depends on the
// placement: resolve with the preferred placement's size, and once more with the other axis's size
// if the resolver moved the popup to that axis.
private fun resolvePosition(
    metrics: PopupMetrics,
    preferredPlacement: OverlayPlacement,
    placementResolver: OverlayPlacementResolver,
    bodySize: IntSize,
): OverlayCalculatedPosition {
    val calculator = OverlayPositionCalculator(
        anchorBounds = metrics.anchorBounds,
        preferredPlacement = preferredPlacement,
        placementResolver = placementResolver,
        edgeMarginPx = metrics.edgeMarginPx,
        anchorSpacingPx = metrics.anchorSpacingPx,
    )
    // The placement is already semantic (left/right), so the calculator must not mirror it again.
    val first = calculator.calculate(
        windowSize = metrics.windowSize,
        layoutDirection = LayoutDirection.Ltr,
        popupContentSize = popupSizeFor(
            placement = preferredPlacement,
            bodySize = bodySize,
            caretInsetPx = metrics.caretInsetPx,
        ),
    )
    if (first.placement.isVertical() == preferredPlacement.isVertical() || metrics.caretInsetPx == 0) {
        return first
    }
    return calculator.calculate(
        windowSize = metrics.windowSize,
        layoutDirection = LayoutDirection.Ltr,
        popupContentSize = popupSizeFor(
            placement = first.placement,
            bodySize = bodySize,
            caretInsetPx = metrics.caretInsetPx,
        ),
    )
}

private fun OverlayPlacement.isVertical(): Boolean {
    return this != OverlayPlacement.Start && this != OverlayPlacement.End
}

private fun OverlayPlacement.hasCaret(): Boolean {
    return when (this) {
        OverlayPlacement.Top,
        OverlayPlacement.Bottom,
        OverlayPlacement.Start,
        OverlayPlacement.End,
        -> true

        OverlayPlacement.TopStart,
        OverlayPlacement.TopEnd,
        OverlayPlacement.BottomStart,
        OverlayPlacement.BottomEnd,
        -> false
    }
}

private fun popupSizeFor(
    placement: OverlayPlacement,
    bodySize: IntSize,
    caretInsetPx: Int,
): IntSize {
    if (!placement.hasCaret()) {
        return bodySize
    }
    return if (placement.isVertical()) {
        IntSize(width = bodySize.width, height = bodySize.height + caretInsetPx)
    } else {
        IntSize(width = bodySize.width + caretInsetPx, height = bodySize.height)
    }
}

// The caret sits on the popup's side facing the anchor; the body is pushed away from it.
private fun bodyOffsetFor(
    placement: OverlayPlacement,
    caretInsetPx: Int,
): IntOffset {
    return when (placement) {
        OverlayPlacement.Bottom -> IntOffset(x = 0, y = caretInsetPx)

        OverlayPlacement.End -> IntOffset(x = caretInsetPx, y = 0)

        OverlayPlacement.Top,
        OverlayPlacement.Start,
        OverlayPlacement.TopStart,
        OverlayPlacement.TopEnd,
        OverlayPlacement.BottomStart,
        OverlayPlacement.BottomEnd,
        -> IntOffset.Zero
    }
}

/** The point of a popup of [popupSize] at [position] that touches the anchor, in the popup's pixels. */
fun anchorContactPoint(
    position: OverlayCalculatedPosition,
    popupSize: IntSize,
): Offset {
    val width = popupSize.width.toFloat()
    val height = popupSize.height.toFloat()
    val centerX = (width / 2f + position.caretCenterOffsetPx).coerceIn(0f, width)
    val centerY = (height / 2f + position.caretCenterOffsetPx).coerceIn(0f, height)
    return when (position.placement) {
        OverlayPlacement.Bottom -> Offset(x = centerX, y = 0f)
        OverlayPlacement.Top -> Offset(x = centerX, y = height)
        OverlayPlacement.End -> Offset(x = 0f, y = centerY)
        OverlayPlacement.Start -> Offset(x = width, y = centerY)
        OverlayPlacement.BottomStart -> Offset(x = width, y = 0f)
        OverlayPlacement.BottomEnd -> Offset.Zero
        OverlayPlacement.TopStart -> Offset(x = width, y = height)
        OverlayPlacement.TopEnd -> Offset(x = 0f, y = height)
    }
}

// Both layers scale around the same point on screen: the pivot is converted into each layer's own
// size, which may put it outside 0..1 for the body — transform origins allow that.
private fun GraphicsLayerScope.applyAnimation(
    animation: AnchoredOverlayAnimation,
    pivot: Offset,
    size: IntSize,
) {
    val scale = animation.scale()
    alpha = animation.alpha()
    scaleX = scale
    scaleY = scale
    transformOrigin = TransformOrigin(
        pivotFractionX = pivot.x / size.width.coerceAtLeast(1),
        pivotFractionY = pivot.y / size.height.coerceAtLeast(1),
    )
}
