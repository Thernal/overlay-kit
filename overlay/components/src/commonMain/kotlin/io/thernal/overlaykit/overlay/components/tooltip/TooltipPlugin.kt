package io.thernal.overlaykit.overlay.components.tooltip

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.core.anchor.OverlayAnchorId
import io.thernal.overlaykit.overlay.core.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.core.modal.OverlayStackEntryController
import io.thernal.overlaykit.overlay.core.placement.OverlayPlacement

internal class TooltipEntry(
    val anchorId: OverlayAnchorId,
    val onDismissRequest: () -> Unit,
    val isVisible: Boolean,
    val placement: OverlayPlacement,
    val style: TooltipStyle,
    val showToken: Int,
    val content: @Composable () -> Unit,
)

internal val LocalTooltipController = staticCompositionLocalOf<OverlayStackEntryController<TooltipEntry>> {
    error("No TooltipPlugin: add TooltipPlugin() to OverlayHost's plugins.")
}

/** Draws `OverlayTooltip` balloons next to their anchors. */
class TooltipPlugin : OverlayLayerPlugin {
    override val key: String = "tooltip"

    @Composable
    override fun Provide(content: @Composable () -> Unit) {
        val controller = remember { OverlayStackEntryController<TooltipEntry>() }

        CompositionLocalProvider(LocalTooltipController provides controller) {
            content()
        }
    }

    @Composable
    override fun BoxScope.Render() {
        val controller = LocalTooltipController.current
        val topEntry by remember(controller) {
            derivedStateOf { controller.topEntry }
        }

        topEntry?.let { entry ->
            TooltipHost(entry = entry)
        }
    }
}
