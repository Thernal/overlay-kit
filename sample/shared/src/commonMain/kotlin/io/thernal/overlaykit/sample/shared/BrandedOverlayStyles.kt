package io.thernal.overlaykit.sample.shared

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.components.dialog.DialogStyle
import io.thernal.overlaykit.overlay.components.dropdown.DropdownStyle
import io.thernal.overlaykit.overlay.components.sheet.BottomSheetStyle
import io.thernal.overlaykit.overlay.components.snackbar.SnackbarStyle
import io.thernal.overlaykit.overlay.components.snackbar.SnackbarTones
import io.thernal.overlaykit.overlay.components.theme.OverlayStyles
import io.thernal.overlaykit.overlay.components.tooltip.TooltipStyle

private val Ink = Color(color = 0xFF14141A)
private val Paper = Color(color = 0xFF23232D)
private val Line = Color(color = 0xFF3A3A48)
private val Text = Color(color = 0xFFF1F0F7)
private val Accent = Color(color = 0xFFB38CFF)

/**
 * What an app writes once: its design tokens mapped onto the overlays' styles. Here a dark,
 * squarer look, to show that every overlay follows it.
 */
internal fun brandedOverlayStyles(): OverlayStyles {
    return OverlayStyles(
        dialog = DialogStyle(
            containerColor = Paper,
            borderColor = Line,
            shape = RoundedCornerShape(12.dp),
        ),
        bottomSheet = BottomSheetStyle(
            containerColor = Paper,
            dragHandleColor = Line,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        ),
        dropdown = DropdownStyle(
            containerColor = Paper,
            borderColor = Line,
            shape = RoundedCornerShape(8.dp),
        ),
        tooltip = TooltipStyle(
            containerColor = Accent,
            cornerRadius = 6.dp,
        ),
        snackbar = SnackbarStyle(
            containerColor = Ink,
            contentColor = Text,
            borderColor = Line,
            tones = SnackbarTones(neutral = Accent),
        ),
    )
}
