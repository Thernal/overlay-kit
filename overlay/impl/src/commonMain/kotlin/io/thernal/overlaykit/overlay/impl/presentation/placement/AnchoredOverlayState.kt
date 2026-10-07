package io.thernal.overlaykit.overlay.impl.presentation.placement

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import io.thernal.overlaykit.overlay.impl.domain.placement.OverlayCalculatedPosition

/**
 * What [AnchoredOverlay] resolved on its last layout — read it in draw or layer blocks (a caret
 * shape, a transform origin), never in composition: it is written during layout.
 */
@Stable
class AnchoredOverlayState {
    var position: OverlayCalculatedPosition? by mutableStateOf(null)
        internal set

    var popupSize: IntSize by mutableStateOf(IntSize.Zero)
        internal set
}
