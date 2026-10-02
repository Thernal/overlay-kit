package io.thernal.overlaykit.overlay.api.presentation.showcase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/** What an [OverlayShowcase] call asks for; the renderer draws it. */
data class ShowcaseParams(
    val isVisible: Boolean,
    val onDismissRequest: () -> Unit,
    val anchorShape: Shape,
    val placement: OverlayPlacement,
    val style: ShowcaseStyle,
)

/** Draws an anchor and, while shown, the dim around it with a balloon. `api` draws the anchor only. */
interface ShowcaseRenderer {
    @Composable
    fun Render(
        params: ShowcaseParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    )
}

/** The renderer when none is installed: the anchor, never the showcase. */
val LocalShowcaseRenderer = staticCompositionLocalOf<ShowcaseRenderer> { AnchorOnlyShowcaseRenderer }

private object AnchorOnlyShowcaseRenderer : ShowcaseRenderer {
    @Composable
    override fun Render(
        params: ShowcaseParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        Box(modifier = modifier, content = content)
    }
}

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
