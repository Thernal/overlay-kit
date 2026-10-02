package io.thernal.overlaykit.overlay.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * False on the first frame after [key] changes, true from the next one. A host targets its enter
 * animation off it, so the first frame draws the overlay at its initial alpha and scale and the
 * animation starts from there — without the producer holding the show back a frame, which races
 * the controller (a tap that opens nothing, a reopen that flashes).
 */
@Composable
internal fun rememberEnterArmed(key: Any?): Boolean {
    var isArmed by remember(key) { mutableStateOf(false) }
    LaunchedEffect(key) {
        isArmed = true
    }
    return isArmed
}
