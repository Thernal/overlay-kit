package io.thernal.overlaykit.overlay.api.presentation.tooltip

import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement

/** What an [OverlayTooltip] call asks for; the renderer draws it. */
data class TooltipParams(
    val isVisible: Boolean?,
    val onVisibilityChange: ((Boolean) -> Unit)?,
    val isToggledByAnchor: Boolean,
    val placement: OverlayPlacement,
    val style: TooltipStyle,
)
