package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarPosition
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle

/**
 * A vertical swipe on the shown message: holding it pauses the message's timer, and releasing it far
 * enough — or fast enough — towards the edge it came from closes it.
 */
internal class SnackbarSwipe(
    private val controller: SnackbarController,
    private val dismissDistancePx: Float,
    private val dismissVelocityPx: Float,
) {
    private var distancePx by mutableFloatStateOf(0f)
    private var isDragging by mutableStateOf(false)

    val draggableState = DraggableState { delta -> onDrag(delta) }

    fun onStopped(
        velocity: Float,
        position: SnackbarPosition,
    ) {
        val shouldDismiss = isPastEdge(velocity = velocity, position = position)
        if (isDragging) {
            controller.onInteractionChanged(isInteracting = false)
        }
        reset()
        if (shouldDismiss) {
            controller.dismiss()
        }
    }

    fun reset() {
        isDragging = false
        distancePx = 0f
    }

    private fun onDrag(delta: Float) {
        if (!isDragging) {
            isDragging = true
            controller.onInteractionChanged(isInteracting = true)
        }
        distancePx += delta
    }

    private fun isPastEdge(
        velocity: Float,
        position: SnackbarPosition,
    ): Boolean {
        return when (position) {
            SnackbarPosition.Top -> distancePx <= -dismissDistancePx || velocity <= -dismissVelocityPx
            SnackbarPosition.Bottom -> distancePx >= dismissDistancePx || velocity >= dismissVelocityPx
        }
    }
}

/** The swipe for the shown message; a message that stops showing ends any interaction with it. */
@Composable
internal fun rememberSnackbarSwipe(
    controller: SnackbarController,
    isShown: Boolean,
    style: SnackbarStyle,
): SnackbarSwipe {
    val density = LocalDensity.current
    val swipe = remember(key1 = controller, key2 = density, key3 = style.swipeDismissDistance) {
        SnackbarSwipe(
            controller = controller,
            dismissDistancePx = with(density) { style.swipeDismissDistance.toPx() },
            dismissVelocityPx = SWIPE_DISMISS_VELOCITY_DP * density.density,
        )
    }
    LaunchedEffect(isShown) {
        if (!isShown) {
            controller.onInteractionChanged(isInteracting = false)
            swipe.reset()
        }
    }
    return swipe
}

/** A press holds the message; a vertical drag is the [swipe]. Nothing reacts while it is not shown. */
internal fun Modifier.snackbarGestures(
    isShown: Boolean,
    controller: SnackbarController,
    swipe: SnackbarSwipe,
    position: SnackbarPosition,
): Modifier {
    return this
        .pointerInput(isShown) {
            if (!isShown) {
                return@pointerInput
            }
            detectTapGestures(
                onPress = {
                    controller.onInteractionChanged(isInteracting = true)
                    tryAwaitRelease()
                    controller.onInteractionChanged(isInteracting = false)
                },
            )
        }
        .draggable(
            orientation = Orientation.Vertical,
            enabled = isShown,
            state = swipe.draggableState,
            onDragStopped = { velocity -> swipe.onStopped(velocity = velocity, position = position) },
        )
}

/** A fling at least this fast (in dp per second) closes the message whatever the distance. */
private const val SWIPE_DISMISS_VELOCITY_DP = 350f
