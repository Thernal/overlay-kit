package io.thernal.overlaykit.overlay.impl.presentation.tooltip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipParams
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipRenderer
import io.thernal.overlaykit.overlay.impl.presentation.anchor.overlayAnchor
import io.thernal.overlaykit.overlay.impl.presentation.anchor.rememberOverlayAnchorId
import io.thernal.overlaykit.overlay.impl.presentation.common.PublishOverlayEntry
import io.thernal.overlaykit.overlay.impl.presentation.common.rememberPublishedWhileExiting

/** Registers the anchor and publishes the balloon to [TooltipPlugin]; keeps the open state when uncontrolled. */
class TooltipRendererImpl : TooltipRenderer {
    @Composable
    override fun Render(
        params: TooltipParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        if (LocalInspectionMode.current) {
            Box(modifier = modifier, content = content)
            return
        }

        val anchorId = rememberOverlayAnchorId(prefix = "tooltip")
        val visibility = rememberTooltipVisibility(params)
        val isPublished = rememberPublishedWhileExiting(
            isShown = visibility.isShown,
            exitMillis = params.style.exitMillis,
        )

        PublishOverlayEntry(
            controller = LocalTooltipController.current,
            isVisible = visibility.isShown || isPublished,
            entry = TooltipEntry(
                anchorId = anchorId,
                onDismissRequest = { visibility.update(isVisible = false) },
                isVisible = visibility.isShown,
                placement = params.placement,
                style = params.style,
                showToken = visibility.showToken,
                content = tooltipContent,
            ),
        )

        Box(
            modifier = modifier
                .overlayAnchor(anchorId)
                .then(
                    if (params.isToggledByAnchor) {
                        Modifier.clickable { visibility.toggle() }
                    } else {
                        Modifier
                    },
                ),
            content = content,
        )
    }
}
