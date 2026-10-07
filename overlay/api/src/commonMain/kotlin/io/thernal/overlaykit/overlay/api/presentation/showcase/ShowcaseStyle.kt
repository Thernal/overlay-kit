package io.thernal.overlaykit.overlay.api.presentation.showcase

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipStyle

/** How an `OverlayShowcase` dims the screen and draws its balloon. */
@Immutable
data class ShowcaseStyle(
    val scrimColor: Color = Color(color = 0xB8000000),
    /** The room left around the highlighted element inside the cut-out. */
    val anchorPadding: Dp = 8.dp,
    val enterMillis: Int = DEFAULT_ENTER_MILLIS,
    val exitMillis: Int = DEFAULT_EXIT_MILLIS,
    val balloon: TooltipStyle = TooltipStyle(autoDismissMillis = 0L),
)

private const val DEFAULT_ENTER_MILLIS = 220
private const val DEFAULT_EXIT_MILLIS = 160
