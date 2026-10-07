package io.thernal.overlaykit.overlay.impl.presentation.host

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.presentation.backdrop.OverlayBackdropStyle
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin

/**
 * Draws [plugins]' overlays above [content]. Apps normally call `api`'s `OverlayHost`, which
 * installs the kit's overlays itself; this one is for a host with only the plugins given.
 *
 * It is [ProvideOverlayControllers] and [OverlayPluginLayers] together. Split them when other app-level
 * providers sit between the two: the controllers must be installed before anything that reads them
 * (a ViewModel routing messages to the snackbar), and the layers must come after anything overlay
 * content needs — overlay content is composed at the layers' position, so a CompositionLocal
 * provided below them is invisible to it.
 */
@Composable
fun OverlayPluginHost(
    plugins: List<OverlayLayerPlugin>,
    modifier: Modifier = Modifier,
    backdrop: OverlayBackdropStyle = OverlayBackdropStyle(),
    content: @Composable () -> Unit,
) {
    ProvideOverlayControllers(plugins = plugins) {
        OverlayPluginLayers(
            modifier = modifier,
            backdrop = backdrop,
            content = content,
        )
    }
}
