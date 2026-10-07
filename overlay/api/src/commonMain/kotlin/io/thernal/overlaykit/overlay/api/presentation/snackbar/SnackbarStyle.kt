package io.thernal.overlaykit.overlay.api.presentation.snackbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayDefaults

/** How the snackbar looks and behaves. Neutral defaults; map an app's tokens in `OverlayStyles`. */
@Immutable
data class SnackbarStyle(
    val containerColor: Color = OverlayDefaults.SurfaceColor,
    val contentColor: Color = OverlayDefaults.ContentColor,
    val borderColor: Color = OverlayDefaults.BorderColor,
    val borderWidth: Dp = 1.dp,
    val shape: Shape = RoundedCornerShape(14.dp),
    /** Around the snackbar, between it and the screen's edges. */
    val outerPadding: PaddingValues = PaddingValues(16.dp),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    val itemSpacing: Dp = 12.dp,
    val stripeWidth: Dp = 4.dp,
    val iconContainerSize: Dp = 32.dp,
    val iconSize: Dp = 20.dp,
    val iconShape: Shape = RoundedCornerShape(10.dp),
    val textStyle: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    val actionTextStyle: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
    val tones: SnackbarTones = SnackbarTones(),
    val icons: SnackbarIcons = SnackbarIcons(),
    /** The padding around the action's label, inside its touch target. */
    val actionPadding: PaddingValues = PaddingValues(4.dp),
    /** How strongly the tone fills the icon's container. */
    val iconBackgroundAlpha: Float = DEFAULT_ICON_BACKGROUND_ALPHA,
    /** A soft wash of the tone along the edge the message comes from. */
    val isGlowShown: Boolean = true,
    val glow: SnackbarGlowStyle = SnackbarGlowStyle(),
    val durationMillis: Long = DEFAULT_DURATION_MILLIS,
    /** How long the message fades in and out. */
    val fadeMillis: Int = DEFAULT_FADE_MILLIS,
    /** How long the message takes to slide away when it closes. */
    val exitSlideMillis: Int = DEFAULT_EXIT_SLIDE_MILLIS,
    /** How far a swipe towards the edge must go to close the message when released slowly. */
    val swipeDismissDistance: Dp = 28.dp,
    val content: SnackbarContent? = null,
)

private const val DEFAULT_DURATION_MILLIS = 2_000L
private const val DEFAULT_FADE_MILLIS = 500
private const val DEFAULT_EXIT_SLIDE_MILLIS = 1200
private const val DEFAULT_ICON_BACKGROUND_ALPHA = 0.14f
