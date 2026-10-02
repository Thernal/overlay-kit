package io.thernal.overlaykit.overlay.components.snackbar

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Shows snackbar messages one at a time, in the order they arrive. Callable from anywhere — a
 * ViewModel's effect collector, a click handler — on the main thread.
 */
interface SnackbarManager {
    /** The message on screen, or leaving it; null when there is none. */
    val current: SnackbarMessage?

    /** False while [current] animates out. */
    val isVisible: Boolean

    fun show(message: SnackbarMessage)

    /** Closes the message on screen; the next one in line follows. */
    fun dismiss()
}

val LocalSnackbarManager = staticCompositionLocalOf<SnackbarManager> {
    error("No SnackbarPlugin: add SnackbarPlugin() to OverlayHost's plugins.")
}
