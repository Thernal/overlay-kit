package io.thernal.overlaykit.overlay.testing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogParams
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogRenderer
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogSurface
import io.thernal.overlaykit.overlay.api.presentation.dialog.LocalDialogRenderer
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownParams
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownRenderer
import io.thernal.overlaykit.overlay.api.presentation.dropdown.LocalDropdownRenderer
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetParams
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetRenderer
import io.thernal.overlaykit.overlay.api.presentation.sheet.BottomSheetSurface
import io.thernal.overlaykit.overlay.api.presentation.sheet.LocalBottomSheetRenderer
import io.thernal.overlaykit.overlay.api.presentation.showcase.LocalShowcaseRenderer
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseParams
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseRenderer
import io.thernal.overlaykit.overlay.api.presentation.tooltip.LocalTooltipRenderer
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipParams
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipRenderer

/**
 * Renderers for UI tests of a screen: no host, no animation, no window — each overlay is drawn in
 * place, after its anchor, while it is visible, so a test finds its content in the screen's own
 * tree. Install with `CompositionLocalProvider(*InlineOverlayRenderers.values().toTypedArray())`.
 *
 * A controlled tooltip shows while `isVisible`; an uncontrolled one never does.
 */
object InlineOverlayRenderers {
    fun values(): List<ProvidedValue<*>> {
        return listOf(
            LocalDialogRenderer provides InlineDialogRenderer,
            LocalBottomSheetRenderer provides InlineBottomSheetRenderer,
            LocalDropdownRenderer provides InlineDropdownRenderer,
            LocalTooltipRenderer provides InlineTooltipRenderer,
            LocalShowcaseRenderer provides InlineShowcaseRenderer,
        )
    }
}

private object InlineDialogRenderer : DialogRenderer {
    @Composable
    override fun Render(
        params: DialogParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        if (params.isVisible) {
            DialogSurface(
                style = params.style,
                horizontalAlignment = params.horizontalAlignment,
                modifier = modifier,
                content = content,
            )
        }
    }
}

private object InlineBottomSheetRenderer : BottomSheetRenderer {
    @Composable
    override fun Render(
        params: BottomSheetParams,
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        if (params.isVisible) {
            BottomSheetSurface(params = params, modifier = modifier, content = content)
        }
    }
}

private object InlineDropdownRenderer : DropdownRenderer {
    @Composable
    override fun Render(
        params: DropdownParams,
        menuContent: @Composable ColumnScope.() -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        Column {
            Box(modifier = modifier, content = content)
            if (params.isExpanded && params.isEnabled) {
                Column(content = menuContent)
            }
        }
    }
}

private object InlineTooltipRenderer : TooltipRenderer {
    @Composable
    override fun Render(
        params: TooltipParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        Column {
            Box(modifier = modifier, content = content)
            if (params.isVisible == true) {
                tooltipContent()
            }
        }
    }
}

private object InlineShowcaseRenderer : ShowcaseRenderer {
    @Composable
    override fun Render(
        params: ShowcaseParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        Column {
            Box(modifier = modifier, content = content)
            if (params.isVisible) {
                tooltipContent()
            }
        }
    }
}
