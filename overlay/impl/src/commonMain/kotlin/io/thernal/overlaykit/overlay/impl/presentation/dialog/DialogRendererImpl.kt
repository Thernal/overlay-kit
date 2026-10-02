package io.thernal.overlaykit.overlay.impl.presentation.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogParams
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogRenderer
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogSurface
import io.thernal.overlaykit.overlay.impl.presentation.common.PublishOverlayEntry

/** Publishes the dialog to [DialogPlugin], which draws it over a scrim at the host. */
class DialogRendererImpl : DialogRenderer {
    @Composable
    override fun Render(
        params: DialogParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        val card: @Composable () -> Unit = {
            DialogSurface(
                style = params.style,
                horizontalAlignment = params.horizontalAlignment,
                modifier = modifier,
                content = content,
            )
        }

        if (LocalInspectionMode.current) {
            card()
            return
        }

        PublishOverlayEntry(
            controller = LocalDialogController.current,
            isVisible = params.isVisible,
            entry = DialogEntry(
                onDismissRequest = params.onDismissRequest,
                alignment = params.alignment,
                isDismissibleOutside = params.isDismissibleOutside,
                isDismissibleByBack = params.isDismissibleByBack,
                style = params.style,
                content = card,
            ),
        )
    }
}
