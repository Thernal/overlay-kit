package io.thernal.overlaykit.overlay.core.host

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

/**
 * One kind of overlay — dialogs, sheets, tooltips — drawn by the components' `OverlayHost` or by
 * [OverlayPluginHost].
 *
 * A plugin has two halves that live at opposite ends of the composition: [Provide] wraps the app's
 * content and installs whatever the overlay's producers need (a controller in a CompositionLocal),
 * and [Render] draws the overlay above that content. Producers anywhere below the host register
 * their entries with the controller; the plugin's [Render] reads them back and draws the top one.
 */
interface OverlayLayerPlugin {
    /**
     * Unique among the host's plugins: it keys the plugin's composition, and two plugins with the
     * same key are refused when the host starts.
     */
    val key: String

    @Composable
    fun Provide(content: @Composable () -> Unit) {
        content()
    }

    @Composable
    fun BoxScope.Render()
}
