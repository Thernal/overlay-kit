package io.thernal.overlaykit.overlay.impl.presentation.modal

import androidx.compose.runtime.MonotonicFrameClock

private const val FRAME_DURATION_NANOS = 16_000_000L

/**
 * `Animatable.animateTo()` waits on `withFrameNanos`, which needs a [MonotonicFrameClock] the
 * Compose UI runtime normally supplies. Run with `runTest(context = ImmediateFrameClock()) { … }`:
 * every frame fires at once at a fixed cadence, so animations finish deterministically.
 */
internal class ImmediateFrameClock : MonotonicFrameClock {
    private var frameTimeNanos = 0L

    override suspend fun <R> withFrameNanos(onFrame: (Long) -> R): R {
        frameTimeNanos += FRAME_DURATION_NANOS
        return onFrame(frameTimeNanos)
    }
}
