package io.thernal.overlaykit.overlay.api.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogStyle
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownStyle
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetStyle
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseStyle
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipStyle

/**
 * The words the overlays say to screen readers. English by default; an app passes its own
 * translations once, through [OverlayTheme].
 */
@Immutable
data class OverlayStrings(
    val dismiss: String = "Dismiss",
    val dialogPane: String = "Dialog",
    val bottomSheetPane: String = "Bottom sheet",
    val menuPane: String = "Menu",
    val showcasePane: String = "Tip",
)

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

private val LocalOverlayStyles = staticCompositionLocalOf { OverlayStyles() }

/**
 * Installs [styles] for the overlays below. Put it above `OverlayHost`: overlays are drawn at the
 * host, and read the styles there as well as at the call site.
 */
@Composable
fun OverlayTheme(
    styles: OverlayStyles,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOverlayStyles provides styles) {
        content()
    }
}

object OverlayTheme {
    val styles: OverlayStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalOverlayStyles.current
}
