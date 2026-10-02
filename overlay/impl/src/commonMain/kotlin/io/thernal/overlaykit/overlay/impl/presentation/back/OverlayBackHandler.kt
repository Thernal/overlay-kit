package io.thernal.overlaykit.overlay.impl.presentation.back

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

/**
 * Closes an overlay on back — the system back button, the predictive back gesture on Android, the
 * edge swipe on iOS — through Compose Multiplatform's navigation events, the same dispatcher
 * Navigation 3 listens to.
 *
 * Call it only while the overlay is on screen (`if (visible) OverlayBackHandler(…)`): the dispatcher
 * gives back to the handler added last, and adding it when the overlay appears puts it above the
 * screens composed before.
 *
 * Returns the gesture's progress, so the overlay can follow the finger before the back completes.
 */
@Composable
fun OverlayBackHandler(
    onBack: () -> Unit,
    isEnabled: Boolean = true,
): OverlayBackProgress {
    if (LocalInspectionMode.current) {
        return OverlayBackProgress.None
    }
    val state = rememberNavigationEventState<NavigationEventInfo>(currentInfo = NavigationEventInfo.None)
    NavigationBackHandler(
        state = state,
        isBackEnabled = isEnabled,
        onBackCompleted = onBack,
    )
    return remember(state) {
        OverlayBackProgress(state = state)
    }
}
