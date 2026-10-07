package io.thernal.overlaykit.overlay.api.presentation.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

/** What an [OverlayBottomSheet] call asks for; the renderer draws it. */
data class BottomSheetParams(
    val isVisible: Boolean,
    val onDismissRequest: () -> Unit,
    val verticalArrangement: Arrangement.Vertical,
    val horizontalAlignment: Alignment.Horizontal,
    val isDismissibleOutside: Boolean,
    val isDismissibleByBack: Boolean,
    val isDraggable: Boolean,
    val style: BottomSheetStyle,
    val dragHandle: (@Composable ColumnScope.() -> Unit)?,
)
