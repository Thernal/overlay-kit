package io.thernal.overlaykit.overlay.core.anchor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onLayoutRectChanged

/**
 * Reports this element's bounds to the [OverlayAnchorRegistry] under [anchorId], and removes them
 * when it leaves the composition.
 *
 * `onLayoutRectChanged` is fed by the layout system's own rectangle index: it runs once per frame
 * the rectangle actually moved, instead of after every global layout pass the way
 * `onGloballyPositioned` does. No throttle and no debounce, so an anchored overlay follows its anchor
 * through a scroll on the same frame.
 */
@Composable
fun Modifier.overlayAnchor(anchorId: OverlayAnchorId): Modifier {
    val registry = LocalOverlayAnchorRegistry.current

    DisposableEffect(key1 = registry, key2 = anchorId) {
        onDispose {
            registry.unregister(anchorId)
        }
    }

    return onLayoutRectChanged(
        throttleMillis = 0,
        debounceMillis = 0,
    ) { bounds ->
        registry.register(
            anchorId = anchorId,
            boundsInRoot = bounds.boundsInRoot,
        )
    }
}
