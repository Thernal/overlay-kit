package io.thernal.overlaykit.overlay.core.back

import androidx.compose.runtime.Stable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationEventState

/** How far a predictive back gesture has gone, 0 to 1; read it in layer or draw blocks. */
@Stable
class OverlayBackProgress internal constructor(
    private val state: NavigationEventState<NavigationEventInfo>?,
) {
    val value: Float
        get() {
            val transition = state?.transitionState as? NavigationEventTransitionState.InProgress
            return transition?.latestEvent?.progress ?: 0f
        }

    companion object {
        val None: OverlayBackProgress = OverlayBackProgress(state = null)
    }
}
