package io.thernal.overlaykit.overlay.api.presentation.dropdown

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayDefaults

/** How an `OverlayDropdown`'s menu looks and moves. Neutral defaults; map an app's tokens in `OverlayStyles`. */
@Immutable
data class DropdownStyle(
    val containerColor: Color = OverlayDefaults.SurfaceColor,
    val borderColor: Color = OverlayDefaults.BorderColor,
    val borderWidth: Dp = 1.dp,
    val shape: Shape = RoundedCornerShape(16.dp),
    /** The closest the menu comes to the host's edges. */
    val edgeMargin: Dp = 16.dp,
    /** The gap between the anchor and the menu. */
    val anchorSpacing: Dp = 4.dp,
    val animationMillis: Int = OverlayDefaults.FADE_MILLIS,
    /** The scale the menu grows from, around the point touching the anchor. */
    val enterScale: Float = DEFAULT_ENTER_SCALE,
)

private const val DEFAULT_ENTER_SCALE = 0.96f
