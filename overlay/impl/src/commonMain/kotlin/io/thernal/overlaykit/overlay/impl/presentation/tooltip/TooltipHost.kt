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
import io.thernal.overlaykit.overlay.api.presentation.tooltip.TooltipStyle
import io.thernal.overlaykit.overlay.impl.presentation.anchor.LocalOverlayAnchorRegistry
import kotlinx.coroutines.delay

@Composable
internal fun TooltipHost(entry: TooltipEntry) {
    val latestOnDismissRequest by rememberUpdatedState(entry.onDismissRequest)
    val dismiss = { latestOnDismissRequest() }
    DismissWhenAnchorGone(entry = entry, dismiss = dismiss)
    DismissAfterDelay(entry = entry, dismiss = dismiss)

    // While the tooltip is up a tap anywhere closes it and goes no further. While it fades out,
    // touches pass through again.
    if (entry.isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(key1 = entry.anchorId, key2 = entry.showToken) {
                    detectTapGestures { dismiss() }
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

/**
 * Bounds can be missing for a moment while the anchor re-registers during a relayout; closing on
 * that would kill the tooltip mid-open. Give it two frames, then close only if it is gone.
 */
@Composable
private fun DismissWhenAnchorGone(
    entry: TooltipEntry,
    dismiss: () -> Unit,
) {
    val anchorRegistry = LocalOverlayAnchorRegistry.current
    LaunchedEffect(key1 = entry.anchorId, key2 = entry.showToken) {
        if (anchorRegistry.getBounds(entry.anchorId) == null) {
            withFrameNanos { }
            withFrameNanos { }
            if (anchorRegistry.getBounds(entry.anchorId) == null) {
                dismiss()
            }
        }
    }
}

/** Closes the tooltip [TooltipStyle.autoDismissMillis] after it shows, when the style sets a delay. */
@Composable
private fun DismissAfterDelay(
    entry: TooltipEntry,
    dismiss: () -> Unit,
) {
    LaunchedEffect(key1 = entry.anchorId, key2 = entry.showToken, key3 = entry.isVisible) {
        if (entry.isVisible && entry.style.autoDismissMillis > 0L) {
            delay(entry.style.autoDismissMillis)
            dismiss()
        }
    }
}
