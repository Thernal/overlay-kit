package io.thernal.overlaykit.overlay.api.presentation.theme

import androidx.compose.runtime.Immutable
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogStyle
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownStyle
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetStyle
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseStyle
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipStyle

/**
 * Every overlay's default look in one value — the place an app maps its design tokens to. Each
 * overlay call can still pass its own style; this is what it gets when it does not.
 */
@Immutable
data class OverlayStyles(
    val dialog: DialogStyle = DialogStyle(),
    val bottomSheet: BottomSheetStyle = BottomSheetStyle(),
    val dropdown: DropdownStyle = DropdownStyle(),
    val tooltip: TooltipStyle = TooltipStyle(),
    val snackbar: SnackbarStyle = SnackbarStyle(),
    val showcase: ShowcaseStyle = ShowcaseStyle(),
    val strings: OverlayStrings = OverlayStrings(),
)
