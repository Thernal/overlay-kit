package io.thernal.overlaykit.overlay.testing

import androidx.compose.runtime.MonotonicFrameClock

private const val FRAME_DURATION_NANOS = 16_000_000L

/**
 * Compose animations wait on `withFrameNanos`, which needs a [MonotonicFrameClock] the UI runtime
 * normally supplies. Run a test with `runTest(context = ImmediateFrameClock()) { … }` and every
 * frame fires at once at a fixed cadence: an overlay state's show and hide animations finish
 * deterministically, without a UI.
 */
class ImmediateFrameClock : MonotonicFrameClock {
    private var frameTimeNanos = 0L

    override suspend fun <R> withFrameNanos(onFrame: (Long) -> R): R {
        frameTimeNanos += FRAME_DURATION_NANOS
        return onFrame(frameTimeNanos)
    }
}
