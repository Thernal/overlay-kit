package io.thernal.overlaykit.overlay.api.presentation.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayDefaults

/** How an `OverlayDialog` looks and moves. Neutral defaults; map an app's tokens in `OverlayStyles`. */
@Immutable
data class DialogStyle(
    val containerColor: Color = OverlayDefaults.SurfaceColor,
    val borderColor: Color = OverlayDefaults.BorderColor,
    val borderWidth: Dp = 1.dp,
    val shape: Shape = RoundedCornerShape(28.dp),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
    val scrimColor: Color = OverlayDefaults.ScrimColor,
    val animationMillis: Int = OverlayDefaults.FADE_MILLIS,
    /** The scale a dialog springs up from. */
    val enterScale: Float = DEFAULT_ENTER_SCALE,
    /** How much smaller the dialog gets as a predictive back gesture completes. */
    val backGestureScale: Float = DEFAULT_BACK_GESTURE_SCALE,
    val isStatusBarPadded: Boolean = true,
    val isNavigationBarPadded: Boolean = true,
) {
    private companion object {
        const val DEFAULT_ENTER_SCALE = 0.82f
        const val DEFAULT_BACK_GESTURE_SCALE = 0.9f
    }
}
