package io.thernal.overlaykit.overlay.api.presentation.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode

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
