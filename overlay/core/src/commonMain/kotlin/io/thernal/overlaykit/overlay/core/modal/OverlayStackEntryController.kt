package io.thernal.overlaykit.overlay.core.modal

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf

/**
 * The controller behind one overlay plugin: every producer (one `OverlayDialog` call, say) keeps
 * its latest entry here under its own key and shows or hides it; the plugin's host draws the top
 * shown entry.
 *
 * Entries are republished on every recomposition of their producer, with fresh lambdas — hosts key
 * their lifecycle on [topKey], never on the entry.
 */
@Stable
class OverlayStackEntryController<E> {
    private val stackController = OverlayStackController<Any>()
    private val entries = mutableStateMapOf<Any, E>()

    val topKey: Any?
        get() = stackController.top

    val topEntry: E?
        get() = stackController.top?.let(entries::get)

    fun show(key: Any) {
        stackController.push(key)
    }

    fun hide(key: Any) {
        stackController.remove(key)
    }

    fun remove(key: Any) {
        hide(key)
        entries.remove(key)
    }

    fun update(
        key: Any,
        entry: E,
    ) {
        entries[key] = entry
    }
}
