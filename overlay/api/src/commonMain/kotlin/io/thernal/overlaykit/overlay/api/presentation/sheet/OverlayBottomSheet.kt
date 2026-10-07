package io.thernal.overlaykit.overlay.api.presentation.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/**
 * A sheet that rises from the bottom edge, above everything below `OverlayHost`. It is as tall as
 * its content.
 *
 * Dragging it down — or scrolling a list inside it past its top — closes it past the style's
 * threshold or on a fling, as do a scrim tap and back; each calls [onDismissRequest], which must set
 * [isVisible] to false. [dragHandle] is drawn above [content]; pass `null` for none.
 */
@Composable
fun OverlayBottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    isDismissibleOutside: Boolean = true,
    isDismissibleByBack: Boolean = true,
    isDraggable: Boolean = true,
    style: BottomSheetStyle = OverlayTheme.styles.bottomSheet,
    dragHandle: (@Composable ColumnScope.() -> Unit)? = { SheetDragHandle(style = style) },
    content: @Composable ColumnScope.() -> Unit,
) {
    LocalBottomSheetRenderer.current.Render(
        params = BottomSheetParams(
            isVisible = isVisible,
            onDismissRequest = onDismissRequest,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            isDismissibleOutside = isDismissibleOutside,
            isDismissibleByBack = isDismissibleByBack,
            isDraggable = isDraggable,
            style = style,
            dragHandle = dragHandle,
        ),
        modifier = modifier,
        content = content,
    )
}
