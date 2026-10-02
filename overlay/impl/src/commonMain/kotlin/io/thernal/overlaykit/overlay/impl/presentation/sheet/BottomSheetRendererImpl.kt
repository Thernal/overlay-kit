package io.thernal.overlaykit.overlay.impl.presentation.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetParams
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetRenderer
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetSurface
import io.thernal.overlaykit.overlay.impl.presentation.common.PublishOverlayEntry

/** Publishes the sheet to [BottomSheetPlugin], which slides it up from the host's bottom edge. */
class BottomSheetRendererImpl : BottomSheetRenderer {
    @Composable
    override fun Render(
        params: BottomSheetParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        val surface: @Composable () -> Unit = {
            BottomSheetSurface(params = params, modifier = modifier, content = content)
        }

        if (LocalInspectionMode.current) {
            surface()
            return
        }

        PublishOverlayEntry(
            controller = LocalBottomSheetController.current,
            isVisible = params.isVisible,
            entry = BottomSheetEntry(
                onDismissRequest = params.onDismissRequest,
                isDismissibleOutside = params.isDismissibleOutside,
                isDismissibleByBack = params.isDismissibleByBack,
                isDraggable = params.isDraggable,
                style = params.style,
                content = surface,
            ),
        )
    }
}
