package io.thernal.overlaykit.sample.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** The sample's palette: dark surfaces, a soft line, one accent. */
@Immutable
data class SampleColors(
    /** The deepest surface: the snackbar. */
    val ink: Color = Color(color = 0xFF14141A),
    /** Raised surfaces: dialogs, sheets, menus. */
    val paper: Color = Color(color = 0xFF23232D),
    val line: Color = Color(color = 0xFF3A3A48),
    val text: Color = Color(color = 0xFFF1F0F7),
    val accent: Color = Color(color = 0xFFB38CFF),
    /** What dims the screen behind a modal. */
    val scrim: Color = Color(color = 0x99000000),
)
