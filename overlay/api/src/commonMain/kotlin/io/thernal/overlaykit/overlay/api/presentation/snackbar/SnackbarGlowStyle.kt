package io.thernal.overlaykit.overlay.api.presentation.snackbar

import androidx.compose.runtime.Immutable

/** The wash of the message's tone along the edge it comes from (`SnackbarStyle.isGlowShown`). */
@Immutable
data class SnackbarGlowStyle(
    /** How long the wash fades in and out. */
    val animationMillis: Int = DEFAULT_GLOW_MILLIS,
    /** How far into the screen the wash reaches, as a share of the screen's height. */
    val heightFraction: Float = DEFAULT_HEIGHT_FRACTION,
    /** The tone's opacity at the edge. */
    val edgeAlpha: Float = DEFAULT_EDGE_ALPHA,
    /** The tone's opacity halfway, before it fades out. */
    val midAlpha: Float = DEFAULT_MID_ALPHA,
)

private const val DEFAULT_GLOW_MILLIS = 800
private const val DEFAULT_HEIGHT_FRACTION = 0.22f
private const val DEFAULT_EDGE_ALPHA = 0.14f
private const val DEFAULT_MID_ALPHA = 0.05f
