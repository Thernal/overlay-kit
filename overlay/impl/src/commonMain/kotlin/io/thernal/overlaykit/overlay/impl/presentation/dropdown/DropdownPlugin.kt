package io.thernal.overlaykit.overlay.impl.presentation.dropdown

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayStackEntryController

internal val LocalDropdownController = staticCompositionLocalOf<OverlayStackEntryController<DropdownEntry>> {
    error("No DropdownPlugin: wrap the app in OverlayHost.")
}

/**
 * Draws `OverlayDropdown` menus next to their anchors. A menu is not modal: the rest of the screen
 * keeps working while it is open, and a press outside it closes it without being swallowed.
 */
class DropdownPlugin : OverlayLayerPlugin {
    override val key: String = "dropdown"

    @Composable
    override fun Provide(content: @Composable () -> Unit) {
        val controller = remember { OverlayStackEntryController<DropdownEntry>() }

        CompositionLocalProvider(LocalDropdownController provides controller) {
            content()
        }
    }

    @Composable
    override fun BoxScope.Render() {
        val controller = LocalDropdownController.current
        val topKey by remember(controller) {
            derivedStateOf { controller.topKey }
        }
        val topEntry by remember(controller) {
            derivedStateOf { controller.topEntry }
        }

        DropdownHost(
            topKey = topKey,
            entry = topEntry,
        )
    }
}
