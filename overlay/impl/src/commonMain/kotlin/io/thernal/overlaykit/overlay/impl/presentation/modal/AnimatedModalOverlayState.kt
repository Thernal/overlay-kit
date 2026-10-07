package io.thernal.overlaykit.overlay.impl.presentation.modal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

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
