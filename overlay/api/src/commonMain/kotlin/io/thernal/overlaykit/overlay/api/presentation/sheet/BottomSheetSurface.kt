package io.thernal.overlaykit.overlay.api.presentation.sheet

import androidx.compose.foundation.background
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
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

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
