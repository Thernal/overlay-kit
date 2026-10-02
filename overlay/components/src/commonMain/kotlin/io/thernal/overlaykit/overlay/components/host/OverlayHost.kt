package io.thernal.overlaykit.overlay.components.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.components.dialog.DialogPlugin
import io.thernal.overlaykit.overlay.components.dropdown.DropdownPlugin
import io.thernal.overlaykit.overlay.components.sheet.BottomSheetPlugin
import io.thernal.overlaykit.overlay.components.showcase.ShowcasePlugin
import io.thernal.overlaykit.overlay.components.snackbar.SnackbarPlugin
import io.thernal.overlaykit.overlay.components.tooltip.TooltipPlugin
import io.thernal.overlaykit.overlay.core.backdrop.OverlayBackdropStyle
import io.thernal.overlaykit.overlay.core.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.core.host.OverlayLayers
import io.thernal.overlaykit.overlay.core.host.ProvideOverlayControllers

/**
 * Every overlay of the kit, then [extraPlugins], in the order that stacks them right, bottom to top:
 * sheets, dialogs (a confirmation opened from a sheet sits above it), dropdowns, showcases,
 * tooltips, the app's own overlays, and the snackbar last — a message stays readable over anything.
 */
fun overlayPlugins(extraPlugins: List<OverlayLayerPlugin> = emptyList()): List<OverlayLayerPlugin> {
    return listOf(
        BottomSheetPlugin(),
        DialogPlugin(),
        DropdownPlugin(),
        ShowcasePlugin(),
        TooltipPlugin(),
    ) + extraPlugins + SnackbarPlugin()
}

/**
 * Draws every overlay of the kit — and [extraPlugins], an app's own — above [content]: one call at
 * the root of the app.
 *
 * Overlay content is composed here, not at its call site, so anything it reads (the app's theme,
 * `OverlayTheme`) is provided above this call. When app-level providers must sit between the
 * overlay controllers and the overlay layers, split it: [ProvideOverlays] … `OverlayLayers`.
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

/**
 * The first half of [OverlayHost]: installs the controllers of every overlay of the kit and of
 * [extraPlugins]. `OverlayLayers` goes further down, around the content.
 */
@Composable
fun ProvideOverlays(
    extraPlugins: List<OverlayLayerPlugin> = emptyList(),
    content: @Composable () -> Unit,
) {
    // Keyed on the plugins' keys, not the list: an app passing `listOf(BannerPlugin())` inline gets
    // the same plugins back on every recomposition instead of a host that re-validates each time.
    val extraKeys = extraPlugins.map { plugin -> plugin.key }
    val plugins = remember(extraKeys) { overlayPlugins(extraPlugins = extraPlugins) }
    ProvideOverlayControllers(
        plugins = plugins,
        content = content,
    )
}
