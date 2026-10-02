package io.thernal.overlaykit.overlay.impl.presentation.host

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import kotlin.test.Test
import kotlin.test.assertEquals

private class BannerPlugin : OverlayLayerPlugin {
    override val key: String = "banner"

    @Composable
    override fun BoxScope.Render() {
        // Nothing to draw in a test.
    }
}

class OverlayPluginsTest {

    @Test
    fun `stacks the kit's overlays bottom to top with the snackbar last`() {
        assertEquals(
            listOf("bottomsheet", "dialog", "dropdown", "showcase", "tooltip", "snackbar"),
            overlayPlugins().map { it.key },
        )
    }

    @Test
    fun `puts an app's own plugins above the kit's overlays and below the snackbar`() {
        assertEquals(
            listOf("bottomsheet", "dialog", "dropdown", "showcase", "tooltip", "banner", "snackbar"),
            overlayPlugins(extraPlugins = listOf(BannerPlugin())).map { it.key },
        )
    }
}
