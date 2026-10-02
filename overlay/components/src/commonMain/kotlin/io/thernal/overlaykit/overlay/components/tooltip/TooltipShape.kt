package io.thernal.overlaykit.overlay.components.tooltip

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.thernal.overlaykit.overlay.core.placement.OverlayPlacement

internal enum class TooltipSharpCorner { TopStart, TopEnd, BottomStart, BottomEnd }

internal class TooltipShape(
    private val cornerRadiusPx: Float,
    private val caretWidthPx: Float,
    private val caretHeightPx: Float,
    private val caretEdge: OverlayPlacement?,
    private val caretCenterOffsetPx: Float,
    private val sharpCorner: TooltipSharpCorner? = null,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val shouldDrawCaret = caretEdge != null && caretWidthPx > 0f && caretHeightPx > 0f
        if (!shouldDrawCaret) {
            return roundedRectOutline(left = 0f, top = 0f, right = size.width, bottom = size.height)
        }
        val resolvedCaretEdge = caretEdge
        val body = caretBodyBounds(size = size, edge = resolvedCaretEdge)
        val roundedRectPath = Path().apply {
            addOutline(roundedRectOutline(left = body.left, top = body.top, right = body.right, bottom = body.bottom))
        }
        val caretPath = buildCaretPath(
            left = body.left,
            top = body.top,
            right = body.right,
            bottom = body.bottom,
            edge = resolvedCaretEdge,
        )
        return Outline.Generic(
            path = Path.combine(
                operation = PathOperation.Union,
                path1 = roundedRectPath,
                path2 = caretPath,
            ),
        )
    }

    private fun caretBodyBounds(
        size: Size,
        edge: OverlayPlacement?,
    ): Rect {
        return when (edge) {
            OverlayPlacement.Top -> Rect(left = 0f, top = caretHeightPx, right = size.width, bottom = size.height)

            OverlayPlacement.Bottom -> Rect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height - caretHeightPx,
            )

            OverlayPlacement.Start -> Rect(left = caretHeightPx, top = 0f, right = size.width, bottom = size.height)

            OverlayPlacement.End -> Rect(left = 0f, top = 0f, right = size.width - caretHeightPx, bottom = size.height)

            null,
            OverlayPlacement.TopStart,
            OverlayPlacement.TopEnd,
            OverlayPlacement.BottomStart,
            OverlayPlacement.BottomEnd,
            -> Rect(left = 0f, top = 0f, right = size.width, bottom = size.height)
        }
    }

    private fun roundedRectOutline(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ): Outline {
        val width = (right - left).coerceAtLeast(0f)
        val height = (bottom - top).coerceAtLeast(0f)
        val radius = cornerRadiusPx.coerceAtMost(minOf(a = width, b = height) / 2f)
        val r = CornerRadius(x = radius, y = radius)
        val zero = CornerRadius.Zero
        return Outline.Rounded(
            RoundRect(
                rect = Rect(left = left, top = top, right = right, bottom = bottom),
                topLeft = if (sharpCorner == TooltipSharpCorner.TopStart) {
                    zero
                } else {
                    r
                },
                topRight = if (sharpCorner == TooltipSharpCorner.TopEnd) {
                    zero
                } else {
                    r
                },
                bottomRight = if (sharpCorner == TooltipSharpCorner.BottomEnd) {
                    zero
                } else {
                    r
                },
                bottomLeft = if (sharpCorner == TooltipSharpCorner.BottomStart) {
                    zero
                } else {
                    r
                },
            ),
        )
    }

    private fun buildCaretPath(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        edge: OverlayPlacement?,
    ): Path {
        val path = Path()
        val caretHalf = caretWidthPx / 2f
        val xCenter = clampCaretCenter(
            center = (left + right) / 2f + if (edge == OverlayPlacement.Top || edge == OverlayPlacement.Bottom) {
                caretCenterOffsetPx
            } else {
                0f
            },
            min = left + cornerRadiusPx + caretHalf,
            max = right - cornerRadiusPx - caretHalf,
        )
        val yCenter = clampCaretCenter(
            center = (top + bottom) / 2f + if (edge == OverlayPlacement.Start || edge == OverlayPlacement.End) {
                caretCenterOffsetPx
            } else {
                0f
            },
            min = top + cornerRadiusPx + caretHalf,
            max = bottom - cornerRadiusPx - caretHalf,
        )

        when (edge) {
            OverlayPlacement.Top -> {
                path.moveTo(x = xCenter - caretHalf, y = top)
                path.lineTo(x = xCenter, y = 0f)
                path.lineTo(x = xCenter + caretHalf, y = top)
                path.close()
            }

            OverlayPlacement.Bottom -> {
                path.moveTo(x = xCenter - caretHalf, y = bottom)
                path.lineTo(x = xCenter, y = bottom + caretHeightPx)
                path.lineTo(x = xCenter + caretHalf, y = bottom)
                path.close()
            }

            OverlayPlacement.Start -> {
                path.moveTo(x = left, y = yCenter - caretHalf)
                path.lineTo(x = 0f, y = yCenter)
                path.lineTo(x = left, y = yCenter + caretHalf)
                path.close()
            }

            OverlayPlacement.End -> {
                path.moveTo(x = right, y = yCenter - caretHalf)
                path.lineTo(x = right + caretHeightPx, y = yCenter)
                path.lineTo(x = right, y = yCenter + caretHalf)
                path.close()
            }

            null,
            OverlayPlacement.TopStart,
            OverlayPlacement.TopEnd,
            OverlayPlacement.BottomStart,
            OverlayPlacement.BottomEnd,
            -> Unit
        }

        return path
    }

    private fun clampCaretCenter(
        center: Float,
        min: Float,
        max: Float,
    ): Float {
        return if (min <= max) {
            center.coerceIn(min, max)
        } else {
            center
        }
    }
}
