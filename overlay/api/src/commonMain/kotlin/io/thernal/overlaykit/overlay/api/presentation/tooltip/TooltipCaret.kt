package io.thernal.overlaykit.overlay.api.presentation.tooltip

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The triangle pointing from a balloon at its anchor. */
@Immutable
data class TooltipCaret(
    val isEnabled: Boolean = true,
    val width: Dp = 16.dp,
    val height: Dp = 8.dp,
)
