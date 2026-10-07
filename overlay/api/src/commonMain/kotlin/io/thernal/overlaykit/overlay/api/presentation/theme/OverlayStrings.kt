package io.thernal.overlaykit.overlay.api.presentation.theme

import androidx.compose.runtime.Immutable

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
