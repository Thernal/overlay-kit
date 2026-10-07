package io.thernal.overlaykit.overlay.api.presentation.tooltip

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

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
