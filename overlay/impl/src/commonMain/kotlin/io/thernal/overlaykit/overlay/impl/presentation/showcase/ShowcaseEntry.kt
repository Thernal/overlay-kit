package io.thernal.overlaykit.overlay.impl.presentation.showcase

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseStyle
import io.thernal.overlaykit.overlay.impl.presentation.anchor.OverlayAnchorId

internal class ShowcaseEntry(
    val anchorId: OverlayAnchorId,
    val anchorShape: Shape,
    val isVisible: Boolean,
    val onDismissRequest: () -> Unit,
    val placement: OverlayPlacement,
    val style: ShowcaseStyle,
    val content: @Composable () -> Unit,
)
