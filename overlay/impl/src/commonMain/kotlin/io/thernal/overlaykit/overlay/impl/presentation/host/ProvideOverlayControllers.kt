package io.thernal.overlaykit.overlay.impl.presentation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.impl.presentation.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.impl.presentation.anchor.OverlayAnchorRegistry
import io.thernal.overlaykit.overlay.impl.presentation.backdrop.LocalOverlayBackdropController
import io.thernal.overlaykit.overlay.impl.presentation.backdrop.OverlayBackdropController

internal val LocalOverlayPlugins = staticCompositionLocalOf<List<OverlayLayerPlugin>> {
    error("No overlay plugins: OverlayPluginLayers must sit inside ProvideOverlayControllers.")
}

/**
 * Installs the overlay controllers — the backdrop, the anchor registry, the modal tracker and each
 * plugin's own `Provide` — as CompositionLocals. It draws nothing; [OverlayPluginLayers] does.
 */
@Composable
fun ProvideOverlayControllers(
    plugins: List<OverlayLayerPlugin>,
    content: @Composable () -> Unit,
) {
    val resolvedPlugins = remember(plugins) { validatePlugins(plugins) }
    val backdropController = remember { OverlayBackdropController() }
    val anchorRegistry = remember { OverlayAnchorRegistry() }
    val modalController = remember { OverlayModalController() }
    val backgroundTouches = remember { OverlayBackgroundTouches() }

    CompositionLocalProvider(
        LocalOverlayBackdropController provides backdropController,
        LocalOverlayAnchorRegistry provides anchorRegistry,
        LocalOverlayModalController provides modalController,
        LocalOverlayBackgroundTouches provides backgroundTouches,
        LocalOverlayPlugins provides resolvedPlugins,
    ) {
        ProvidePlugins(
            plugins = resolvedPlugins,
            index = 0,
            content = content,
        )
    }
}

@Composable
private fun ProvidePlugins(
    plugins: List<OverlayLayerPlugin>,
    index: Int,
    content: @Composable () -> Unit,
) {
    val head = plugins.getOrNull(index)
    if (head == null) {
        content()
        return
    }

    key(head.key) {
        head.Provide {
            ProvidePlugins(
                plugins = plugins,
                index = index + 1,
                content = content,
            )
        }
    }
}

private fun validatePlugins(plugins: List<OverlayLayerPlugin>): List<OverlayLayerPlugin> {
    plugins.forEach { plugin ->
        check(plugin.key.isNotBlank()) {
            "Overlay plugin key cannot be blank."
        }
    }

    val duplicateKeys = plugins
        .groupBy { plugin -> plugin.key }
        .filterValues { samePlugins -> samePlugins.size > 1 }
        .keys
        .sorted()
    check(duplicateKeys.isEmpty()) {
        "Duplicate overlay plugin key(s): ${duplicateKeys.joinToString()}. " +
            "Each OverlayLayerPlugin key must be unique."
    }

    return plugins.toList()
}
