package io.thernal.overlaykit.overlay.api.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * The neutral values the default styles share — a light surface, a soft border, a dark scrim —
 * so an app that has not mapped its tokens yet still gets readable overlays.
 */
internal object OverlayDefaults {
    val SurfaceColor = Color(color = 0xFFFFFFFF)
    val BorderColor = Color(color = 0x1F000000)
    val ScrimColor = Color(color = 0x66000000)
    val ContentColor = Color(color = 0xFF1B1B1F)
    val InverseSurfaceColor = Color(color = 0xFF2F3033)
    val InverseContentColor = Color(color = 0xFFF2F0F4)

    const val FADE_MILLIS = 150
    const val SHEET_MILLIS = 300
}
