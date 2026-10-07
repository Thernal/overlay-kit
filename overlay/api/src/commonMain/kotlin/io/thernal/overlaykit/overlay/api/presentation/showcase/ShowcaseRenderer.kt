package io.thernal.overlaykit.overlay.api.presentation.showcase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

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
