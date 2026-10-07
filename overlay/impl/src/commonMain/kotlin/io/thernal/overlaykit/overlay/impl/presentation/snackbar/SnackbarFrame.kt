package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarPosition
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle

/** The message clear of the system bar on its edge, announced politely: the app's row or the kit's. */
@Composable
internal fun SnackbarFrame(
    message: SnackbarMessage,
    style: SnackbarStyle,
    controller: SnackbarController,
) {
    Box(
        modifier = Modifier
            .systemBarPadding(message.position)
            .padding(style.outerPadding)
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        val onAction = {
            message.onAction?.invoke()
            controller.dismiss()
        }
        val custom = style.content
        if (custom != null) {
            custom.Content(message = message, onAction = onAction)
        } else {
            SnackbarRow(message = message, style = style, onAction = onAction)
        }
    }
}

private fun Modifier.systemBarPadding(position: SnackbarPosition): Modifier {
    return when (position) {
        SnackbarPosition.Top -> statusBarsPadding()
        SnackbarPosition.Bottom -> navigationBarsPadding()
    }
}
