package io.thernal.overlaykit.overlay.impl.presentation.anchor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect

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

/**
 * Where every anchor is. Anchors report their bounds in the root's coordinates
 * ([overlayAnchor]); the host reports its own origin there too, and [getBounds] answers in the
 * host's coordinates — so the overlays stay right even when the host does not start at the
 * window's corner (an app drawn under a toolbar, a split screen).
 */
@Stable
class OverlayAnchorRegistry {
    private val anchors = mutableStateMapOf<OverlayAnchorId, IntRect>()

    var hostOriginInRoot: IntOffset by mutableStateOf(IntOffset.Zero)
        internal set

    fun register(
        anchorId: OverlayAnchorId,
        boundsInRoot: IntRect,
    ) {
        if (anchors[anchorId] != boundsInRoot) {
            anchors[anchorId] = boundsInRoot
        }
    }

    fun unregister(anchorId: OverlayAnchorId) {
        anchors.remove(anchorId)
    }

    /** The anchor's bounds in the host's coordinates, or null while it is not laid out. */
    fun getBounds(anchorId: OverlayAnchorId): IntRect? {
        return anchors[anchorId]?.translate(-hostOriginInRoot)
    }
}

val LocalOverlayAnchorRegistry = staticCompositionLocalOf<OverlayAnchorRegistry> {
    error("No OverlayAnchorRegistry: wrap the app in OverlayHost (or ProvideOverlayControllers).")
}
