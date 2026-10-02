package io.thernal.overlaykit.overlay.components.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.components.common.OverlayDefaults

/** How an `OverlayBottomSheet` looks and moves. Neutral defaults; map an app's tokens in `OverlayStyles`. */
@Immutable
data class BottomSheetStyle(
    val containerColor: Color = OverlayDefaults.SurfaceColor,
    val shape: Shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    val scrimColor: Color = OverlayDefaults.ScrimColor,
    val animationMillis: Int = OverlayDefaults.SHEET_MILLIS,
    val dragHandleColor: Color = OverlayDefaults.BorderColor,
    val dragHandleSize: DpSize = DpSize(width = 38.dp, height = 4.dp),
    val dragHandlePadding: Dp = 12.dp,
    /** The share of the sheet's height it must be dragged down by to close when released slowly. */
    val dismissThreshold: Float = DEFAULT_DISMISS_THRESHOLD,
    /** How far down the sheet moves, as a share of its height, as a predictive back gesture completes. */
    val backGestureShift: Float = DEFAULT_BACK_GESTURE_SHIFT,
    val isStatusBarPadded: Boolean = true,
    val isNavigationBarPadded: Boolean = true,
    val isImePadded: Boolean = true,
) {
    private companion object {
        const val DEFAULT_DISMISS_THRESHOLD = 0.35f
        const val DEFAULT_BACK_GESTURE_SHIFT = 0.1f
    }
}
