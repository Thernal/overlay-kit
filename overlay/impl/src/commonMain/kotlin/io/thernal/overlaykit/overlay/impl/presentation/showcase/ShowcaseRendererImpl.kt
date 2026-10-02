package io.thernal.overlaykit.overlay.impl.presentation.showcase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseParams
import io.thernal.overlaykit.overlay.api.presentation.showcase.ShowcaseRenderer
import io.thernal.overlaykit.overlay.impl.presentation.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.impl.presentation.anchor.overlayAnchor
import io.thernal.overlaykit.overlay.impl.presentation.anchor.rememberOverlayAnchorId
import io.thernal.overlaykit.overlay.impl.presentation.common.PublishOverlayEntry
import kotlinx.coroutines.delay

/** Registers the anchor and publishes the showcase to [ShowcasePlugin] once the anchor has bounds. */
class ShowcaseRendererImpl : ShowcaseRenderer {
    @Composable
    override fun Render(
        params: ShowcaseParams,
        tooltipContent: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        if (LocalInspectionMode.current) {
            Box(modifier = modifier, content = content)
            return
        }

        val anchorId = rememberOverlayAnchorId(prefix = "showcase")
        val anchorRegistry = LocalOverlayAnchorRegistry.current
        // Shown only once the anchor has bounds: a cut-out needs somewhere to be.
        val isShown = params.isVisible && anchorRegistry.getBounds(anchorId) != null

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
            controller = LocalShowcaseController.current,
            isVisible = isShown || isPublished,
            entry = ShowcaseEntry(
                anchorId = anchorId,
                anchorShape = params.anchorShape,
                isVisible = isShown,
                onDismissRequest = params.onDismissRequest,
                placement = params.placement,
                style = params.style,
                content = tooltipContent,
            ),
        )

        Box(
            modifier = modifier.overlayAnchor(anchorId),
            content = content,
        )
    }
}
