package io.thernal.overlaykit.overlay.api.presentation.snackbar

import androidx.compose.runtime.Composable
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage

/** Replaces the whole snackbar row; the host still animates it, places it and handles swipes. */
fun interface SnackbarContent {
    @Composable
    fun Content(
        message: SnackbarMessage,
        onAction: () -> Unit,
    )
}
