package io.thernal.overlaykit.overlay.impl.presentation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Which overlays currently block the content below them. While any does, [OverlayLayers] hides that
 * content from screen readers and keeps keyboard focus from wandering into it — what a separate
 * window gives Material's dialogs for free, and an in-tree overlay has to do itself.
 */
@Stable
class OverlayModalController {
    private val owners = mutableStateListOf<Any>()
    private var isFocusToRestore = false

    internal var saveBackgroundFocus: () -> Boolean = { false }
    internal var restoreBackgroundFocus: () -> Unit = {}

    val isModalOpen: Boolean
        get() = owners.isNotEmpty()

    /**
     * Remembers what had focus in the content, if anything did, so it gets focus back once the
     * last modal closes. True when there was something: the caller should then move focus into
     * its overlay — that also closes a keyboard a focused text field had open, as a dialog window
     * would. Nothing moves for a user who has not focused anything.
     */
    fun takeBackgroundFocus(): Boolean {
        val wasSaved = saveBackgroundFocus()
        if (wasSaved) {
            isFocusToRestore = true
        }
        return wasSaved
    }

    internal fun onAllModalsClosed() {
        if (isFocusToRestore) {
            isFocusToRestore = false
            restoreBackgroundFocus()
        }
    }

    fun open(owner: Any) {
        if (owner !in owners) {
            owners.add(owner)
        }
    }

    fun close(owner: Any) {
        owners.remove(owner)
    }
}

val LocalOverlayModalController = staticCompositionLocalOf<OverlayModalController> {
    error("No OverlayModalController: wrap the app in OverlayHost (or ProvideOverlayControllers).")
}

/**
 * Marks [owner] as a blocking overlay while [isActive]; a host calls it for as long as its modal
 * overlay is on screen.
 */
@Composable
fun OverlayModalEffect(
    owner: Any,
    isActive: Boolean,
) {
    val controller = LocalOverlayModalController.current
    DisposableEffect(key1 = controller, key2 = owner, key3 = isActive) {
        if (isActive) {
            controller.open(owner)
        } else {
            controller.close(owner)
        }
        onDispose {
            controller.close(owner)
        }
    }
}
