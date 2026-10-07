package io.thernal.overlaykit.overlay.impl.presentation.dialog

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogStyle
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.impl.presentation.modal.AnimatedModalOverlayState
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayStackEntryController

internal val LocalDialogController = staticCompositionLocalOf<OverlayStackEntryController<DialogEntry>> {
    error("No DialogPlugin: wrap the app in OverlayHost.")
}

private val LocalDialogState = staticCompositionLocalOf<AnimatedModalOverlayState<DialogEntry>> {
    error("No DialogPlugin: wrap the app in OverlayHost.")
}

/** Draws `OverlayDialog`s: the most recently shown one on top, over a scrim. */
class DialogPlugin : OverlayLayerPlugin {
    override val key: String = "dialog"

    @Composable
    override fun Provide(content: @Composable () -> Unit) {
        val controller = remember { OverlayStackEntryController<DialogEntry>() }
        val state = remember { AnimatedModalOverlayState<DialogEntry>(animationMillis = DialogStyle().animationMillis) }

        CompositionLocalProvider(
            LocalDialogController provides controller,
            LocalDialogState provides state,
        ) {
            content()
        }
    }

    @Composable
    override fun BoxScope.Render() {
        val controller = LocalDialogController.current
        val topKey by remember(controller) {
            derivedStateOf { controller.topKey }
        }
        val topEntry by remember(controller) {
            derivedStateOf { controller.topEntry }
        }

        DialogHost(
            topKey = topKey,
            topEntry = topEntry,
            state = LocalDialogState.current,
        )
    }
}
