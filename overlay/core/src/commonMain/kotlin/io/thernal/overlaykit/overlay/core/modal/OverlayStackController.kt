package io.thernal.overlaykit.overlay.core.modal

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf

/** The order overlays were shown in; the last one shown is on top. Showing one again moves it up. */
@Stable
class OverlayStackController<T> {
    private val stack = mutableStateListOf<T>()

    val top: T?
        get() = stack.lastOrNull()

    fun push(item: T) {
        stack.remove(item)
        stack.add(item)
    }

    fun remove(item: T) {
        stack.remove(item)
    }
}
