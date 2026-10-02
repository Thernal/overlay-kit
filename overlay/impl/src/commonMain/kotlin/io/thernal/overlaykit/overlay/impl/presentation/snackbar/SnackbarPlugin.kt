package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.api.presentation.snackbar.LocalSnackbarManager
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

private val LocalSnackbarController = staticCompositionLocalOf<SnackbarController> {
    error("No SnackbarPlugin: wrap the app in OverlayHost.")
}

/**
 * Draws snackbar messages and installs [LocalSnackbarManager] for everything below the host. The
 * queue runs for as long as the host is composed.
 */
class SnackbarPlugin : OverlayLayerPlugin {
    override val key: String = "snackbar"

    @Composable
    override fun Provide(content: @Composable () -> Unit) {
        val style = OverlayTheme.styles.snackbar
        val controller = remember { SnackbarController(defaultDurationMillis = style.durationMillis) }
        SideEffect {
            controller.defaultDurationMillis = style.durationMillis
        }
        LaunchedEffect(controller) {
            controller.run()
        }

        CompositionLocalProvider(
            LocalSnackbarController provides controller,
            LocalSnackbarManager provides controller,
        ) {
            content()
        }
    }

    @Composable
    override fun BoxScope.Render() {
        SnackbarHost(
            controller = LocalSnackbarController.current,
            style = OverlayTheme.styles.snackbar,
        )
    }
}
