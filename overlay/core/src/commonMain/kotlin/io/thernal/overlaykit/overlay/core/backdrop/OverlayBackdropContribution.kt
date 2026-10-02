package io.thernal.overlaykit.overlay.core.backdrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Feeds [progressProvider] into the backdrop under [owner] for as long as this is composed. The
 * provider is read in a snapshot flow, not in composition, so an animating progress pushes the
 * backdrop without recomposing anything.
 */
@Composable
fun OverlayBackdropContribution(
    owner: String,
    progressProvider: () -> Float,
) {
    val backdropController = LocalOverlayBackdropController.current
    val latestProgressProvider by rememberUpdatedState(progressProvider)

    LaunchedEffect(key1 = backdropController, key2 = owner) {
        snapshotFlow { latestProgressProvider().coerceIn(0f, 1f) }
            .collectLatest { progress ->
                backdropController.setProgress(
                    owner = owner,
                    progress = progress,
                )
            }
    }

    DisposableEffect(key1 = backdropController, key2 = owner) {
        onDispose {
            backdropController.clear(owner)
        }
    }
}
