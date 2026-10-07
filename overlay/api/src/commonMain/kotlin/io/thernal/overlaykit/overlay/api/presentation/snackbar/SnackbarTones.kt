package io.thernal.overlaykit.overlay.api.presentation.snackbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarKind
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
