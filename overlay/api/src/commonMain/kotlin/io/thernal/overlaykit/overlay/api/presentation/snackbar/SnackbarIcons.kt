package io.thernal.overlaykit.overlay.api.presentation.snackbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarKind

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
