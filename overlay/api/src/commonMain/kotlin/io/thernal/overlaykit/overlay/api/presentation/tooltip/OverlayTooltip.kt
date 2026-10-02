package io.thernal.overlaykit.overlay.api.presentation.tooltip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/** What an [OverlayTooltip] call asks for; the renderer draws it. */
data class TooltipParams(
    val isVisible: Boolean?,
    val onVisibilityChange: ((Boolean) -> Unit)?,
    val isToggledByAnchor: Boolean,
    val placement: OverlayPlacement,
    val style: TooltipStyle,
)

/** Draws an anchor and its tooltip balloon. `api` draws the anchor only. */
interface TooltipRenderer {
    @Composable
    fun Render(
        params: TooltipParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    )
}

/** The renderer when none is installed: the anchor, never the balloon. */
val LocalTooltipRenderer = staticCompositionLocalOf<TooltipRenderer> { AnchorOnlyTooltipRenderer }

private object AnchorOnlyTooltipRenderer : TooltipRenderer {
    @Composable
    override fun Render(
        params: TooltipParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        Box(modifier = modifier, content = content)
    }
}

/**
 * [content] with a tooltip balloon. A tap on [content] toggles it, it hides itself after the
 * style's `autoDismissMillis`, and a tap anywhere closes it.
 *
 * Leave [isVisible] null to let the tooltip keep its own state; pass it, with
 * [onVisibilityChange], to drive it from outside.
 */
@Composable
fun OverlayTooltip(
    tooltipContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean? = null,
    onVisibilityChange: ((Boolean) -> Unit)? = null,
    isToggledByAnchor: Boolean = true,
    placement: OverlayPlacement = OverlayPlacement.Bottom,
    style: TooltipStyle = OverlayTheme.styles.tooltip,
    content: @Composable BoxScope.() -> Unit,
) {
    require(isVisible == null || onVisibilityChange != null) {
        "onVisibilityChange must be provided when isVisible is controlled."
    }
    LocalTooltipRenderer.current.Render(
        params = TooltipParams(
            isVisible = isVisible,
            onVisibilityChange = onVisibilityChange,
            isToggledByAnchor = isToggledByAnchor,
            placement = placement,
            style = style,
        ),
        tooltipContent = tooltipContent,
        modifier = modifier,
        content = content,
    )
}
