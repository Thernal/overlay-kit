package io.thernal.overlaykit.overlay.components.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.components.common.PublishOverlayEntry
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme

/**
 * A sheet that rises from the bottom edge, drawn by [BottomSheetPlugin] above everything below
 * `OverlayHost`. It is as tall as its content.
 *
 * Dragging it down — or scrolling a list inside it past its top — closes it past the style's
 * threshold or on a fling, as do a scrim tap and back; each calls [onDismissRequest], which must set
 * [isVisible] to false. [dragHandle] is drawn above [content]; pass `null` for none. In a preview
 * the sheet's surface is drawn in place.
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
    val surface: @Composable () -> Unit = {
        Column(
            modifier = modifier
                .then(
                    if (style.isStatusBarPadded) {
                        Modifier.statusBarsPadding()
                    } else {
                        Modifier
                    },
                )
                .background(color = style.containerColor, shape = style.shape)
                .fillMaxWidth()
                .padding(style.contentPadding)
                .then(
                    if (style.isImePadded) {
                        Modifier.imePadding()
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (style.isNavigationBarPadded) {
                        Modifier.navigationBarsPadding()
                    } else {
                        Modifier
                    },
                ),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            dragHandle?.invoke(this)
            content()
        }
    }

    if (LocalInspectionMode.current) {
        surface()
        return
    }

    PublishOverlayEntry(
        controller = LocalBottomSheetController.current,
        isVisible = isVisible,
        entry = BottomSheetEntry(
            onDismissRequest = onDismissRequest,
            isDismissibleOutside = isDismissibleOutside,
            isDismissibleByBack = isDismissibleByBack,
            isDraggable = isDraggable,
            style = style,
            content = surface,
        ),
    )
}

/** The pill at the top of a sheet that says it can be dragged. */
@Composable
fun ColumnScope.SheetDragHandle(
    modifier: Modifier = Modifier,
    style: BottomSheetStyle = OverlayTheme.styles.bottomSheet,
) {
    Box(
        modifier = modifier
            .align(Alignment.CenterHorizontally)
            .padding(vertical = style.dragHandlePadding)
            .size(style.dragHandleSize)
            .background(color = style.dragHandleColor, shape = CircleShape),
    )
}
