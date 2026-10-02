package io.thernal.overlaykit.overlay.impl.presentation.tooltip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipParams
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipRenderer
import io.thernal.overlaykit.overlay.impl.presentation.anchor.overlayAnchor
import io.thernal.overlaykit.overlay.impl.presentation.anchor.rememberOverlayAnchorId
import io.thernal.overlaykit.overlay.impl.presentation.common.PublishOverlayEntry
import kotlinx.coroutines.delay

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
        var isOpenInternally by remember { mutableStateOf(false) }
        var showToken by remember { mutableIntStateOf(0) }
        val isShown = params.isVisible ?: isOpenInternally

        val updateVisibility: (Boolean) -> Unit = { isNextVisible ->
            if (params.isVisible == null) {
                isOpenInternally = isNextVisible
            }
            params.onVisibilityChange?.invoke(isNextVisible)
        }

        // The entry stays on the stack while the balloon fades out, so the host can draw the exit;
        // it is hidden once the exit has run.
        var isPublished by remember { mutableStateOf(false) }
        LaunchedEffect(isShown) {
            if (isShown) {
                isPublished = true
            } else {
                if (isPublished && params.style.exitMillis > 0) {
                    delay(params.style.exitMillis.toLong())
                }
                isPublished = false
            }
        }

        PublishOverlayEntry(
            controller = LocalTooltipController.current,
            isVisible = isShown || isPublished,
            entry = TooltipEntry(
                anchorId = anchorId,
                onDismissRequest = { updateVisibility(false) },
                isVisible = isShown,
                placement = params.placement,
                style = params.style,
                showToken = showToken,
                content = tooltipContent,
            ),
        )

        Box(
            modifier = modifier
                .overlayAnchor(anchorId)
                .then(
                    if (params.isToggledByAnchor) {
                        Modifier.clickable {
                            val isNextVisible = !isShown
                            if (isNextVisible) {
                                showToken += 1
                            }
                            updateVisibility(isNextVisible)
                        }
                    } else {
                        Modifier
                    },
                ),
            content = content,
        )
    }
}
