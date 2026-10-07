package io.thernal.overlaykit.overlay.impl.presentation.showcase

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayStackEntryController

internal val LocalShowcaseController = staticCompositionLocalOf<OverlayStackEntryController<ShowcaseEntry>> {
    error("No ShowcasePlugin: wrap the app in OverlayHost.")
}

/** Draws `OverlayShowcase`s: the screen dimmed around one element, a balloon pointing at it. */
class ShowcasePlugin : OverlayLayerPlugin {
    override val key: String = "showcase"

    @Composable
    override fun Provide(content: @Composable () -> Unit) {
        val controller = remember { OverlayStackEntryController<ShowcaseEntry>() }

        CompositionLocalProvider(LocalShowcaseController provides controller) {
            content()
        }
    }

    @Composable
    override fun BoxScope.Render() {
        val controller = LocalShowcaseController.current
        val topEntry by remember(controller) {
            derivedStateOf { controller.topEntry }
        }

        topEntry?.let { entry ->
            ShowcaseHost(entry = entry)
        }
    }
}
