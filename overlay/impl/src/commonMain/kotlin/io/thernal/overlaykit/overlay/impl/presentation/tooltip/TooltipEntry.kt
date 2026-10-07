package io.thernal.overlaykit.overlay.impl.presentation.tooltip

import androidx.compose.runtime.Composable
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipStyle
import io.thernal.overlaykit.overlay.impl.presentation.anchor.OverlayAnchorId

internal class TooltipEntry(
    val anchorId: OverlayAnchorId,
    val onDismissRequest: () -> Unit,
    val isVisible: Boolean,
    val placement: OverlayPlacement,
    val style: TooltipStyle,
    val showToken: Int,
    val content: @Composable () -> Unit,
)
