package io.thernal.overlaykit.overlay.impl.presentation.anchor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember

/** Names one anchor — the element a dropdown, tooltip or showcase points at. */
@Immutable
data class OverlayAnchorId(val value: String)

// Composition runs on one thread, so a plain counter cannot race. Ids derived from hash codes were
// dropped in the source app: two keys may share a hash and silently overwrite each other's bounds.
private var anchorIdCounter = 0

@Composable
fun rememberOverlayAnchorId(prefix: String): OverlayAnchorId {
    return remember {
        OverlayAnchorId("$prefix-${anchorIdCounter++}")
    }
}
