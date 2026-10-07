package io.thernal.overlaykit.overlay.api.presentation.dropdown

import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement

/** What an [OverlayDropdown] call asks for; the renderer draws it. */
data class DropdownParams(
    val isExpanded: Boolean,
    val onExpandedChange: (Boolean) -> Unit,
    val isEnabled: Boolean,
    val placement: OverlayPlacement,
    val isWidthMatchingAnchor: Boolean,
    val isToggledByAnchor: Boolean,
    val isDismissibleOutside: Boolean,
    val style: DropdownStyle,
)
