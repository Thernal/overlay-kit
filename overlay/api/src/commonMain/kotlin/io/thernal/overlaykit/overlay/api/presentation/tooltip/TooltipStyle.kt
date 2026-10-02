package io.thernal.overlaykit.overlay.api.presentation.tooltip

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayDefaults

/** The triangle pointing from a balloon at its anchor. */
@Immutable
data class TooltipCaret(
    val isEnabled: Boolean = true,
    val width: Dp = 16.dp,
    val height: Dp = 8.dp,
)

/** How an `OverlayTooltip` balloon looks and moves. Neutral defaults; map an app's tokens in `OverlayStyles`. */
@Immutable
data class TooltipStyle(
    val containerColor: Color = OverlayDefaults.InverseSurfaceColor,
    val borderColor: Color = Color.Transparent,
    val borderWidth: Dp = 0.dp,
    val cornerRadius: Dp = 12.dp,
    val caret: TooltipCaret = TooltipCaret(),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    /** The closest the balloon comes to the host's edges. */
    val edgeMargin: Dp = 16.dp,
    /** The gap between the anchor and the caret's tip. */
    val anchorSpacing: Dp = 12.dp,
    val enterMillis: Int = DEFAULT_ENTER_MILLIS,
    val exitMillis: Int = DEFAULT_EXIT_MILLIS,
    /** The scale the balloon pops from, around the caret's tip. */
    val initialScale: Float = DEFAULT_INITIAL_SCALE,
    /** How long a tooltip stays before it hides itself; 0 keeps it until dismissed. */
    val autoDismissMillis: Long = DEFAULT_AUTO_DISMISS_MILLIS,
) {
    private companion object {
        const val DEFAULT_ENTER_MILLIS = 180
        const val DEFAULT_EXIT_MILLIS = 200
        const val DEFAULT_INITIAL_SCALE = 0.82f
        const val DEFAULT_AUTO_DISMISS_MILLIS = 2_500L
    }
}
