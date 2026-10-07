package io.thernal.overlaykit.overlay.api.presentation.dialog

import androidx.compose.ui.Alignment

/** What an [OverlayDialog] call asks for; the renderer draws it. */
data class DialogParams(
    val isVisible: Boolean,
    val onDismissRequest: () -> Unit,
    val alignment: Alignment,
    val horizontalAlignment: Alignment.Horizontal,
    val isDismissibleOutside: Boolean,
    val isDismissibleByBack: Boolean,
    val style: DialogStyle,
)
