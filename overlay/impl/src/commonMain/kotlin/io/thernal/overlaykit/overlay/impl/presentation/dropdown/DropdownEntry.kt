package io.thernal.overlaykit.overlay.impl.presentation.dropdown

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownStyle
import io.thernal.overlaykit.overlay.impl.presentation.anchor.OverlayAnchorId

internal class DropdownEntry(
    val anchorId: OverlayAnchorId,
    val onDismissRequest: () -> Unit,
    val placement: OverlayPlacement,
    val isWidthMatchingAnchor: Boolean,
    val isDismissibleOutside: Boolean,
    val style: DropdownStyle,
    val menuContent: @Composable ColumnScope.() -> Unit,
)
