package io.thernal.overlaykit.overlay.testing

import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarManager
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage

/**
 * A [SnackbarManager] for tests: it shows nothing and keeps every message it was given, so a test
 * of a ViewModel or a screen asserts what would have been said. The newest message counts as
 * [current] until [dismiss].
 */
class RecordingSnackbarManager : SnackbarManager {
    private val recorded = mutableListOf<SnackbarMessage>()

    val messages: List<SnackbarMessage>
        get() = recorded.toList()

    var dismissCount: Int = 0
        private set

    override var current: SnackbarMessage? = null
        private set

    override val isVisible: Boolean
        get() = current != null

    override fun show(message: SnackbarMessage) {
        recorded.add(message)
        current = message
    }

    override fun dismiss() {
        dismissCount++
        current = null
    }

    fun clear() {
        recorded.clear()
        dismissCount = 0
        current = null
    }
}
