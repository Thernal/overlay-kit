package io.thernal.overlaykit.overlay.api.presentation.snackbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarKind
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayDefaults

/** The accent of each [SnackbarKind]: the stripe, the icon, the action, the glow. */
@Immutable
data class SnackbarTones(
    val neutral: Color = OverlayDefaults.ContentColor,
    val info: Color = Color(color = 0xFF2F6FED),
    val success: Color = Color(color = 0xFF1E9E5A),
    val warning: Color = Color(color = 0xFFE0A100),
    val error: Color = Color(color = 0xFFD93A3A),
) {
    fun of(kind: SnackbarKind): Color {
        return when (kind) {
            SnackbarKind.Neutral -> neutral
            SnackbarKind.Info -> info
            SnackbarKind.Success -> success
            SnackbarKind.Warning -> warning
            SnackbarKind.Error -> error
        }
    }
}

/** An icon per [SnackbarKind], tinted with its tone. None by default: the kit ships no icon set. */
@Immutable
data class SnackbarIcons(
    val neutral: ImageVector? = null,
    val info: ImageVector? = null,
    val success: ImageVector? = null,
    val warning: ImageVector? = null,
    val error: ImageVector? = null,
) {
    fun of(kind: SnackbarKind): ImageVector? {
        return when (kind) {
            SnackbarKind.Neutral -> neutral
            SnackbarKind.Info -> info
            SnackbarKind.Success -> success
            SnackbarKind.Warning -> warning
            SnackbarKind.Error -> error
        }
    }
}

/** Replaces the whole snackbar row; the host still animates it, places it and handles swipes. */
fun interface SnackbarContent {
    @Composable
    fun Content(
        message: SnackbarMessage,
        onAction: () -> Unit,
    )
}

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
    /** A soft wash of the tone along the edge the message comes from. */
    val isGlowShown: Boolean = true,
    val durationMillis: Long = DEFAULT_DURATION_MILLIS,
    /** How far a swipe towards the edge must go to close the message when released slowly. */
    val swipeDismissDistance: Dp = 28.dp,
    val content: SnackbarContent? = null,
) {
    private companion object {
        const val DEFAULT_DURATION_MILLIS = 2_000L
    }
}
