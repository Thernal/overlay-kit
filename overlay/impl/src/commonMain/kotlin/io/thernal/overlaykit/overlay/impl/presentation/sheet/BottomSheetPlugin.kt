package io.thernal.overlaykit.overlay.impl.presentation.sheet

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayLayerPlugin
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetStyle
import io.thernal.overlaykit.overlay.impl.presentation.backdrop.OverlayBackdropContribution
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayStackEntryController

internal class BottomSheetEntry(
    val onDismissRequest: () -> Unit,
    val isDismissibleOutside: Boolean,
    val isDismissibleByBack: Boolean,
    val isDraggable: Boolean,
    val style: BottomSheetStyle,
    val content: @Composable () -> Unit,
)

internal val LocalBottomSheetController = staticCompositionLocalOf<OverlayStackEntryController<BottomSheetEntry>> {
    error("No BottomSheetPlugin: wrap the app in OverlayHost.")
}

private val LocalBottomSheetState = staticCompositionLocalOf<BottomSheetState> {
    error("No BottomSheetPlugin: wrap the app in OverlayHost.")
}

private const val BOTTOM_SHEET_BACKDROP_OWNER = "bottomsheet"

/**
 * Draws `OverlayBottomSheet`s from the bottom edge, over a scrim, and pushes the content behind
 * them back (the host's backdrop style) as they rise.
 */
class BottomSheetPlugin : OverlayLayerPlugin {
    override val key: String = "bottomsheet"

    @Composable
    override fun Provide(content: @Composable () -> Unit) {
        val controller = remember { OverlayStackEntryController<BottomSheetEntry>() }
        val state = remember { BottomSheetState(animationMillis = BottomSheetStyle().animationMillis) }

        CompositionLocalProvider(
            LocalBottomSheetController provides controller,
            LocalBottomSheetState provides state,
        ) {
            content()
        }
    }

    @Composable
    override fun BoxScope.Render() {
        val controller = LocalBottomSheetController.current
        val state = LocalBottomSheetState.current
        val topKey by remember(controller) {
            derivedStateOf { controller.topKey }
        }
        val topEntry by remember(controller) {
            derivedStateOf { controller.topEntry }
        }

        OverlayBackdropContribution(owner = BOTTOM_SHEET_BACKDROP_OWNER) { state.progress }

        BottomSheetHost(
            topKey = topKey,
            topEntry = topEntry,
            state = state,
        )
    }
}
