package io.thernal.overlaykit.overlay.api.presentation.dropdown

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/** Draws an anchor and its menu. `impl` places the menu next to the anchor; `api` draws the anchor only. */
interface DropdownRenderer {
    @Composable
    fun Render(
        params: DropdownParams,
        menuContent: @Composable ColumnScope.() -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    )
}

/** The renderer when none is installed: the anchor, never the menu — in a preview and elsewhere. */
val LocalDropdownRenderer = staticCompositionLocalOf<DropdownRenderer> { AnchorOnlyDropdownRenderer }

private object AnchorOnlyDropdownRenderer : DropdownRenderer {
    @Composable
    override fun Render(
        params: DropdownParams,
        menuContent: @Composable ColumnScope.() -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        Box(modifier = modifier, content = content)
    }
}
