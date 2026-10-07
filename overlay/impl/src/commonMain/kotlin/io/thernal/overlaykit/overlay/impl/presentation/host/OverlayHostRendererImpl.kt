package io.thernal.overlaykit.overlay.impl.presentation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.presentation.backdrop.OverlayBackdropStyle
import io.thernal.overlaykit.overlay.api.presentation.dialog.LocalDialogRenderer
import io.thernal.overlaykit.overlay.api.presentation.dropdown.LocalDropdownRenderer
import io.thernal.overlaykit.overlay.api.presentation.host.LocalOverlayHostRenderer
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayHostRenderer
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.api.presentation.sheet.LocalBottomSheetRenderer
import io.thernal.overlaykit.overlay.api.presentation.showcase.LocalShowcaseRenderer
import io.thernal.overlaykit.overlay.api.presentation.tooltip.LocalTooltipRenderer
import io.thernal.overlaykit.overlay.impl.presentation.dialog.DialogPlugin
import io.thernal.overlaykit.overlay.impl.presentation.dialog.DialogRendererImpl
import io.thernal.overlaykit.overlay.impl.presentation.dropdown.DropdownPlugin
import io.thernal.overlaykit.overlay.impl.presentation.dropdown.DropdownRendererImpl
import io.thernal.overlaykit.overlay.impl.presentation.sheet.BottomSheetPlugin
import io.thernal.overlaykit.overlay.impl.presentation.sheet.BottomSheetRendererImpl
import io.thernal.overlaykit.overlay.impl.presentation.showcase.ShowcasePlugin
import io.thernal.overlaykit.overlay.impl.presentation.showcase.ShowcaseRendererImpl
import io.thernal.overlaykit.overlay.impl.presentation.snackbar.SnackbarPlugin
import io.thernal.overlaykit.overlay.impl.presentation.tooltip.TooltipPlugin
import io.thernal.overlaykit.overlay.impl.presentation.tooltip.TooltipRendererImpl

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
 * Every renderer of the kit as a `ProvidedValue`, for a root to install with
 * `CompositionLocalProvider` — what `OverlayProvidersModule` contributes to an app graph, for an app without
 * one.
 */
fun overlayRenderers(): List<ProvidedValue<*>> {
    return listOf(
        LocalOverlayHostRenderer provides OverlayHostRendererImpl(),
        LocalDialogRenderer provides DialogRendererImpl(),
        LocalBottomSheetRenderer provides BottomSheetRendererImpl(),
        LocalDropdownRenderer provides DropdownRendererImpl(),
        LocalTooltipRenderer provides TooltipRendererImpl(),
        LocalShowcaseRenderer provides ShowcaseRendererImpl(),
    )
}

/** Installs every overlay of the kit and the app's plugins, and draws them above the content. */
class OverlayHostRendererImpl : OverlayHostRenderer {
    @Composable
    override fun Provide(
        extraPlugins: List<OverlayLayerPlugin>,
        content: @Composable () -> Unit,
    ) {
        // Keyed on the plugins' keys, not the list: an app passing `listOf(BannerPlugin())` inline
        // gets the same plugins back on every recomposition instead of a host that re-validates.
        val extraKeys = extraPlugins.map { plugin -> plugin.key }
        val plugins = remember(extraKeys) { overlayPlugins(extraPlugins = extraPlugins) }
        ProvideOverlayControllers(
            plugins = plugins,
            content = content,
        )
    }

    @Composable
    override fun Layers(
        modifier: Modifier,
        backdrop: OverlayBackdropStyle,
        content: @Composable () -> Unit,
    ) {
        OverlayPluginLayers(
            modifier = modifier,
            backdrop = backdrop,
            content = content,
        )
    }
}
