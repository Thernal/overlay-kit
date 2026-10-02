package io.thernal.overlaykit.overlay.impl.presentation.modal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

/**
 * The lifecycle of a modal overlay host — which entry is on screen, whether it is still drawn while
 * it animates out — independent of how its progress moves. [AnimatedModalOverlayState] moves it
 * with an `Animatable`; a bottom sheet moves it with its drag state.
 *
 * The host drives it from three places: a `LaunchedEffect(topKey)` calling [onTopEntryChanged], a
 * `SideEffect` calling [syncTopEntry], and a `LaunchedEffect` calling [animateInIfReady].
 */
@Stable
abstract class ModalOverlayState<E> {
    private var currentEntryKey: Any? = null

    var isDismissing: Boolean by mutableStateOf(false)
        protected set

    var shouldRender: Boolean by mutableStateOf(false)
        private set

    var currentEntry: E? by mutableStateOf(null)
        private set

    /** 0 hidden, 1 fully shown. */
    abstract val progress: Float

    fun scrimAlpha(scrimAlpha: Float): Float {
        return (progress * scrimAlpha).coerceIn(0f, 1f)
    }

    suspend fun onTopEntryChanged(
        topKey: Any?,
        topEntry: E?,
    ) {
        if (topKey != null) {
            isDismissing = false
            if (currentEntryKey != null && currentEntryKey != topKey) {
                snapHidden()
                onEntrySwapped()
            }
            currentEntryKey = topKey
            if (topEntry != null) {
                currentEntry = topEntry
            }
            shouldRender = true
            return
        }

        val dismissedEntryKey = currentEntryKey ?: return
        isDismissing = true
        try {
            animateOut()
        } finally {
            // A show that interrupts the exit cancels it; the state is reset all the same, and the
            // new entry is taken up by the next onTopEntryChanged.
            if (currentEntryKey == dismissedEntryKey) {
                shouldRender = false
                currentEntry = null
                currentEntryKey = null
                withContext(NonCancellable) {
                    snapHidden()
                }
            }
            isDismissing = false
        }
    }

    /**
     * Keeps [currentEntry] fresh when the producer republishes the entry for the key already shown.
     * Show, swap and dismiss stay in [onTopEntryChanged], which hosts key on the top key only.
     */
    @Suppress("CanBeNonNullable") // Hosts pass the controller's nullable top straight through.
    fun syncTopEntry(
        topKey: Any?,
        topEntry: E?,
    ) {
        if (topKey != null && topKey == currentEntryKey && topEntry != null) {
            currentEntry = topEntry
        }
    }

    suspend fun animateInIfReady() {
        if (currentEntry != null && !isDismissing && progress < 1f) {
            animateIn()
        }
    }

    suspend fun ensureHiddenWhenNotRendering() {
        if (!shouldRender && progress != 0f) {
            snapHidden()
        }
    }

    protected abstract suspend fun animateIn()

    protected abstract suspend fun animateOut()

    protected abstract suspend fun snapHidden()

    /** Called when another entry replaces the one on screen; reset per-entry state here. */
    protected open fun onEntrySwapped() {
        // Nothing per entry by default.
    }
}

/** A [ModalOverlayState] whose progress is a tween of [animationMillis], in and out. */
@Stable
open class AnimatedModalOverlayState<E>(
    animationMillis: Int,
) : ModalOverlayState<E>() {
    private val animatable = Animatable(initialValue = 0f)

    /** The host updates it from the entry's style before each show. */
    var animationMillis: Int by mutableIntStateOf(animationMillis)

    override val progress: Float
        get() = animatable.value.coerceIn(0f, 1f)

    // Animatable runs one animation at a time and cancels the one before: a show interrupting a
    // hide continues from the current value with no lock of our own.
    override suspend fun animateIn() {
        animatable.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = animationMillis))
    }

    override suspend fun animateOut() {
        animatable.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = animationMillis))
    }

    override suspend fun snapHidden() {
        animatable.snapTo(targetValue = 0f)
    }

    protected suspend fun snapProgress(value: Float) {
        animatable.snapTo(targetValue = value.coerceIn(0f, 1f))
    }
}
