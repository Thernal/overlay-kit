package io.thernal.overlaykit.overlay.components.snackbar

import androidx.compose.runtime.Immutable

enum class SnackbarKind {
    Neutral,
    Info,
    Success,
    Warning,
    Error,
}

enum class SnackbarPosition {
    Top,
    Bottom,
}

/**
 * One message for the snackbar. The kit draws [text] and [actionLabel] as they are — an app with
 * its own string type (arch-kit's `UiString`) resolves it before calling `show`.
 *
 * [durationMillis] overrides the style's for this message. [onDismiss] runs once the message has
 * left, however it left.
 */
@Immutable
data class SnackbarMessage(
    val text: String,
    val kind: SnackbarKind = SnackbarKind.Neutral,
    val position: SnackbarPosition = SnackbarPosition.Top,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null,
    val durationMillis: Long? = null,
)
