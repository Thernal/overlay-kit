package io.thernal.overlaykit.overlay.api.presentation.snackbar

import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage

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

/**
 * The host's snackbar, installed by `OverlayHost`. Outside a host — a preview, a test without one —
 * it is a manager that drops every message, so a screen that shows messages still composes.
 */
val LocalSnackbarManager = staticCompositionLocalOf<SnackbarManager> { DroppingSnackbarManager }

private object DroppingSnackbarManager : SnackbarManager {
    override val current: SnackbarMessage? = null
    override val isVisible: Boolean = false

    override fun show(message: SnackbarMessage) {
        // No host: nowhere to show it.
    }

    override fun dismiss() {
        // No host: nothing is shown.
    }
}
