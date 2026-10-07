package io.thernal.overlaykit.overlay.impl.presentation.sheet

import androidx.compose.runtime.Composable
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetStyle

internal class BottomSheetEntry(
    val onDismissRequest: () -> Unit,
    val isDismissibleOutside: Boolean,
    val isDismissibleByBack: Boolean,
    val isDraggable: Boolean,
    val style: BottomSheetStyle,
    val content: @Composable () -> Unit,
)
