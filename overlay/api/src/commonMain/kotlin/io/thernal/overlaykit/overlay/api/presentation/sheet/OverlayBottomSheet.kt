package io.thernal.overlaykit.overlay.api.presentation.sheet

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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/** What an [OverlayBottomSheet] call asks for; the renderer draws it. */
data class BottomSheetParams(
    val isVisible: Boolean,
    val onDismissRequest: () -> Unit,
    val verticalArrangement: Arrangement.Vertical,
    val horizontalAlignment: Alignment.Horizontal,
    val isDismissibleOutside: Boolean,
    val isDismissibleByBack: Boolean,
    val isDraggable: Boolean,
    val style: BottomSheetStyle,
    val dragHandle: (@Composable ColumnScope.() -> Unit)?,
)

/** Draws bottom sheets. `impl` slides them up from the bottom edge; `api` only previews them. */
interface BottomSheetRenderer {
    @Composable
    fun Render(
        params: BottomSheetParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    )
}

/** The renderer when none is installed: the sheet's surface in place in a preview, nothing elsewhere. */
val LocalBottomSheetRenderer = staticCompositionLocalOf<BottomSheetRenderer> { PreviewBottomSheetRenderer }

private object PreviewBottomSheetRenderer : BottomSheetRenderer {
    @Composable
    override fun Render(
        params: BottomSheetParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        if (LocalInspectionMode.current) {
            BottomSheetSurface(params = params, modifier = modifier, content = content)
        }
    }
}

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

/** The sheet's surface as the kit draws it: the drag handle, then [content], on the style's container. */
@Composable
fun BottomSheetSurface(
    params: BottomSheetParams,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val style = params.style
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
        verticalArrangement = params.verticalArrangement,
        horizontalAlignment = params.horizontalAlignment,
    ) {
        params.dragHandle?.invoke(this)
        content()
    }
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
