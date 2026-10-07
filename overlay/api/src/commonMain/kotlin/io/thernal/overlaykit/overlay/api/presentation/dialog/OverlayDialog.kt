package io.thernal.overlaykit.overlay.api.presentation.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

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
