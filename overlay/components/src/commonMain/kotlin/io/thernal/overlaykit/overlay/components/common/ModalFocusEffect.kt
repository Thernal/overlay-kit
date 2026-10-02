package io.thernal.overlaykit.overlay.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import io.thernal.overlaykit.overlay.core.host.LocalOverlayModalController

/**
 * Moves focus into a modal overlay when it appears, if the content had focus — a focused text
 * field's keyboard closes, and a keyboard user lands in the overlay. The content gets its focus
 * back when the last modal closes (`OverlayLayers`). Nothing moves for a user who had focused
 * nothing.
 */
@Composable
internal fun ModalFocusEffect(
    entryKey: Any?,
    isShown: Boolean,
    focusRequester: FocusRequester,
) {
    val modalController = LocalOverlayModalController.current
    LaunchedEffect(key1 = entryKey, key2 = isShown) {
        if (isShown && modalController.takeBackgroundFocus()) {
            focusRequester.requestFocus()
        }
    }
}
