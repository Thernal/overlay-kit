package io.thernal.overlaykit.overlay.impl.presentation.tooltip

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import io.thernal.overlaykit.overlay.impl.presentation.anchor.LocalOverlayAnchorRegistry
import kotlinx.coroutines.delay

@Composable
internal fun TooltipHost(entry: TooltipEntry) {
    val anchorRegistry = LocalOverlayAnchorRegistry.current
    val latestOnDismissRequest by rememberUpdatedState(entry.onDismissRequest)

    // Bounds can be missing for a moment while the anchor re-registers during a relayout; closing
    // on that would kill the tooltip mid-open. Give it two frames, then close only if it is gone.
    LaunchedEffect(key1 = entry.anchorId, key2 = entry.showToken) {
        if (anchorRegistry.getBounds(entry.anchorId) == null) {
            withFrameNanos { }
            withFrameNanos { }
            if (anchorRegistry.getBounds(entry.anchorId) == null) {
                latestOnDismissRequest()
            }
        }
    }

    LaunchedEffect(key1 = entry.anchorId, key2 = entry.showToken, key3 = entry.isVisible) {
        if (entry.isVisible && entry.style.autoDismissMillis > 0L) {
            delay(entry.style.autoDismissMillis)
            latestOnDismissRequest()
        }
    }

    // While the tooltip is up a tap anywhere closes it and goes no further. While it fades out,
    // touches pass through again.
    if (entry.isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(key1 = entry.anchorId, key2 = entry.showToken) {
                    detectTapGestures {
                        latestOnDismissRequest()
                    }
                },
        )
    }

    TooltipBalloon(
        anchorId = entry.anchorId,
        placement = entry.placement,
        style = entry.style,
        isVisible = entry.isVisible,
        showKey = entry.showToken,
        content = entry.content,
    )
}
