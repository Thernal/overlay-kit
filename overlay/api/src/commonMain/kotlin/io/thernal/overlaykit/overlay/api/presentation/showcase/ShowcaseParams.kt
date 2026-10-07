package io.thernal.overlaykit.overlay.api.presentation.showcase

import androidx.compose.ui.graphics.Shape
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement

/** What an [OverlayShowcase] call asks for; the renderer draws it. */
data class ShowcaseParams(
    val isVisible: Boolean,
    val onDismissRequest: () -> Unit,
    val anchorShape: Shape,
    val placement: OverlayPlacement,
    val style: ShowcaseStyle,
)
