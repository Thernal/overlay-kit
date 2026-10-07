package io.thernal.overlaykit.overlay.api.presentation.showcase

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/**
 * Highlights [content]: the screen dims everywhere except a cut-out of [anchorShape] around it, and
 * a balloon with [tooltipContent] points at it. A tap anywhere, or back, calls [onDismissRequest] —
 * persist "already shown" there and set [isVisible] to false. There is no auto-dismiss.
 */
@Composable
fun OverlayShowcase(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    tooltipContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    anchorShape: Shape = RectangleShape,
    placement: OverlayPlacement = OverlayPlacement.Bottom,
    style: ShowcaseStyle = OverlayTheme.styles.showcase,
    content: @Composable BoxScope.() -> Unit,
) {
    LocalShowcaseRenderer.current.Render(
        params = ShowcaseParams(
            isVisible = isVisible,
            onDismissRequest = onDismissRequest,
            anchorShape = anchorShape,
            placement = placement,
            style = style,
        ),
        tooltipContent = tooltipContent,
        modifier = modifier,
        content = content,
    )
}
