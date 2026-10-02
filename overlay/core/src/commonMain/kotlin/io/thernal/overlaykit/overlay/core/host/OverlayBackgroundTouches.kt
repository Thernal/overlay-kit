package io.thernal.overlaykit.overlay.core.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset

/**
 * Presses that land on the app's content rather than on an overlay, in host coordinates.
 *
 * Compose delivers a touch to one of two overlapping siblings, never both, so an overlay that
 * caught touches outside itself would take them from the content. [OverlayLayers] observes them on
 * the content's side instead, without consuming, and passes them here: an overlay can close on an
 * outside press and the press still reaches what it landed on.
 */
@Stable
class OverlayBackgroundTouches {
    private val listeners = mutableListOf<(Offset) -> Unit>()

    fun addListener(listener: (Offset) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (Offset) -> Unit) {
        listeners.remove(listener)
    }

    fun dispatchPress(position: Offset) {
        listeners.toList().forEach { listener ->
            listener(position)
        }
    }
}

val LocalOverlayBackgroundTouches = staticCompositionLocalOf<OverlayBackgroundTouches> {
    error("No OverlayBackgroundTouches: wrap the app in OverlayHost (or ProvideOverlayControllers).")
}

/** Calls [onPress] for every press on the content while [isEnabled]. */
@Composable
fun OverlayBackgroundPressEffect(
    isEnabled: Boolean,
    onPress: (Offset) -> Unit,
) {
    val touches = LocalOverlayBackgroundTouches.current
    val latestOnPress by rememberUpdatedState(onPress)
    DisposableEffect(key1 = touches, key2 = isEnabled) {
        val listener: (Offset) -> Unit = { position ->
            latestOnPress(position)
        }
        if (isEnabled) {
            touches.addListener(listener)
        }
        onDispose {
            touches.removeListener(listener)
        }
    }
}
