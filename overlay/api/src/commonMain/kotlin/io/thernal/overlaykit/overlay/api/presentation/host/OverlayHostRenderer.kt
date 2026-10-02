package io.thernal.overlaykit.overlay.api.presentation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.presentation.backdrop.OverlayBackdropStyle

/**
 * The root's half of the overlays: [Provide] installs their controllers around the app,
 * [Layers] draws the content and the overlays above it. `impl` installs every overlay of the kit
 * and the app's [OverlayLayerPlugin]s; `api` draws the content alone.
 */
interface OverlayHostRenderer {
    @Composable
    fun Provide(
        extraPlugins: List<OverlayLayerPlugin>,
        content: @Composable () -> Unit,
    )

    @Composable
    fun Layers(
        modifier: Modifier,
        backdrop: OverlayBackdropStyle,
        content: @Composable () -> Unit,
    )
}

/** The renderer when none is installed: the content, no overlays — previews of whole screens work. */
val LocalOverlayHostRenderer = staticCompositionLocalOf<OverlayHostRenderer> { ContentOnlyOverlayHostRenderer }

private object ContentOnlyOverlayHostRenderer : OverlayHostRenderer {
    @Composable
    override fun Provide(
        extraPlugins: List<OverlayLayerPlugin>,
        content: @Composable () -> Unit,
    ) {
        content()
    }

    @Composable
    override fun Layers(
        modifier: Modifier,
        backdrop: OverlayBackdropStyle,
        content: @Composable () -> Unit,
    ) {
        content()
    }
}

/**
 * Draws every overlay of the kit — and [extraPlugins], an app's own — above [content]: one call at
 * the root of the app, inside the renderers' `CompositionLocalProvider`.
 *
 * Overlay content is composed here, not at its call site, so anything it reads (the app's theme,
 * `OverlayTheme`) is provided above this call. When app-level providers must sit between the
 * overlay controllers and the overlay layers, split it: [ProvideOverlays] … [OverlayLayers].
 */
@Composable
fun OverlayHost(
    modifier: Modifier = Modifier,
    extraPlugins: List<OverlayLayerPlugin> = emptyList(),
    backdrop: OverlayBackdropStyle = OverlayBackdropStyle(),
    content: @Composable () -> Unit,
) {
    ProvideOverlays(extraPlugins = extraPlugins) {
        OverlayLayers(
            modifier = modifier,
            backdrop = backdrop,
            content = content,
        )
    }
}

/** The first half of [OverlayHost]: installs the overlays' controllers. [OverlayLayers] goes below it. */
@Composable
fun ProvideOverlays(
    extraPlugins: List<OverlayLayerPlugin> = emptyList(),
    content: @Composable () -> Unit,
) {
    LocalOverlayHostRenderer.current.Provide(
        extraPlugins = extraPlugins,
        content = content,
    )
}

/** The second half of [OverlayHost]: the content, and the overlays above it. */
@Composable
fun OverlayLayers(
    modifier: Modifier = Modifier,
    backdrop: OverlayBackdropStyle = OverlayBackdropStyle(),
    content: @Composable () -> Unit,
) {
    LocalOverlayHostRenderer.current.Layers(
        modifier = modifier,
        backdrop = backdrop,
        content = content,
    )
}
