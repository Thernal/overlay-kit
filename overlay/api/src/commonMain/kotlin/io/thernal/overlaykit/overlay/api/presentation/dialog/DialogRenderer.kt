package io.thernal.overlaykit.overlay.api.presentation.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode

/** Draws dialogs. `impl` draws them over a scrim, above everything; `api` only previews them. */
interface DialogRenderer {
    @Composable
    fun Render(
        params: DialogParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    )
}

/**
 * The renderer when none is installed: in a preview it draws the dialog's card in place, so a
 * screen previews without `impl`; anywhere else nothing — a dialog that never appears shows a
 * missing installation at once.
 */
val LocalDialogRenderer = staticCompositionLocalOf<DialogRenderer> { PreviewDialogRenderer }

private object PreviewDialogRenderer : DialogRenderer {
    @Composable
    override fun Render(
        params: DialogParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        if (LocalInspectionMode.current) {
            DialogSurface(
                style = params.style,
                horizontalAlignment = params.horizontalAlignment,
                modifier = modifier,
                content = content,
            )
        }
    }
}
