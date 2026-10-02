package io.thernal.overlaykit.overlay.api.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/** What an [OverlayDialog] call asks for; the renderer draws it. */
data class DialogParams(
    val isVisible: Boolean,
    val onDismissRequest: () -> Unit,
    val alignment: Alignment,
    val horizontalAlignment: Alignment.Horizontal,
    val isDismissibleOutside: Boolean,
    val isDismissibleByBack: Boolean,
    val style: DialogStyle,
)

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

/**
 * A dialog over a scrim, above everything below `OverlayHost`.
 *
 * [onDismissRequest] is called on a scrim tap and on back; it must set [isVisible] to false for the
 * dialog to leave. Several dialogs may be visible at once: the one shown last is on top, and the
 * others come back as it leaves.
 */
@Composable
fun OverlayDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    isDismissibleOutside: Boolean = true,
    isDismissibleByBack: Boolean = true,
    style: DialogStyle = OverlayTheme.styles.dialog,
    content: @Composable ColumnScope.() -> Unit,
) {
    LocalDialogRenderer.current.Render(
        params = DialogParams(
            isVisible = isVisible,
            onDismissRequest = onDismissRequest,
            alignment = alignment,
            horizontalAlignment = horizontalAlignment,
            isDismissibleOutside = isDismissibleOutside,
            isDismissibleByBack = isDismissibleByBack,
            style = style,
        ),
        modifier = modifier,
        content = content,
    )
}

/** The dialog's card as the kit draws it: [content] on the style's container, border and padding. */
@Composable
fun DialogSurface(
    style: DialogStyle,
    horizontalAlignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(color = style.containerColor, shape = style.shape)
            .border(width = style.borderWidth, color = style.borderColor, shape = style.shape)
            .padding(style.contentPadding),
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}
