package io.thernal.overlaykit.overlay.impl.presentation.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogStyle

internal class DialogEntry(
    val onDismissRequest: () -> Unit,
    val alignment: Alignment,
    val isDismissibleOutside: Boolean,
    val isDismissibleByBack: Boolean,
    val style: DialogStyle,
    val content: @Composable () -> Unit,
)
