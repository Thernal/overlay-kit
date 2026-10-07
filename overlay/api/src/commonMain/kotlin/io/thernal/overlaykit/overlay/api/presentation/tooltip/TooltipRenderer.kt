package io.thernal.overlaykit.overlay.api.presentation.tooltip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

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
