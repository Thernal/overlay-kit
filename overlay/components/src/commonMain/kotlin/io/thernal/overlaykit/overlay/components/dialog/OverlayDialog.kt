package io.thernal.overlaykit.overlay.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.components.common.PublishOverlayEntry
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme

/**
 * A dialog over a scrim, drawn by [DialogPlugin] above everything below `OverlayHost`.
 *
 * [onDismissRequest] is called on a scrim tap and on back; it must set [isVisible] to false for the
 * dialog to leave. Several dialogs may be visible at once: the one shown last is on top, and the
 * others come back as it leaves. In a preview the dialog's card is drawn in place.
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
    val card: @Composable () -> Unit = {
        Column(
            modifier = modifier
                .background(color = style.containerColor, shape = style.shape)
                .border(width = style.borderWidth, color = style.borderColor, shape = style.shape)
                .padding(style.contentPadding),
            horizontalAlignment = horizontalAlignment,
            content = content,
        )
    }

    if (LocalInspectionMode.current) {
        card()
        return
    }

    PublishOverlayEntry(
        controller = LocalDialogController.current,
        isVisible = isVisible,
        entry = DialogEntry(
            onDismissRequest = onDismissRequest,
            alignment = alignment,
            isDismissibleOutside = isDismissibleOutside,
            isDismissibleByBack = isDismissibleByBack,
            style = style,
            content = card,
        ),
    )
}
