package io.thernal.overlaykit.overlay.impl.presentation.back

import androidx.compose.runtime.Composable

/** The back gesture's progress for a modal while it is shown; none while it is on its way out. */
@Composable
internal fun modalBackProgress(
    isShown: Boolean,
    onBack: () -> Unit,
    isEnabled: Boolean,
): OverlayBackProgress {
    return if (isShown) {
        OverlayBackHandler(onBack = onBack, isEnabled = isEnabled)
    } else {
        OverlayBackProgress.None
    }
}
