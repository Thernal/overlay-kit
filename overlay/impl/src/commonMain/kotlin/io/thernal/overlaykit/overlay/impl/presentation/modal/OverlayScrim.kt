package io.thernal.overlaykit.overlay.impl.presentation.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick

/**
 * The dim behind a modal overlay. A tap on it asks to dismiss — unless [isDismissible] is false or
 * the overlay is already leaving — and screen readers see it as one button labelled
 * [dismissLabel], as Material's scrims are.
 *
 * [alpha] is read in the layer block, so an animating scrim redraws without recomposing.
 */
@Composable
fun BoxScope.OverlayScrim(
    color: Color,
    alpha: () -> Float,
    isDismissing: Boolean,
    onDismissRequest: () -> Unit,
    dismissLabel: String,
    isDismissible: Boolean = true,
) {
    // Producers recreate onDismissRequest on every recomposition; keying pointerInput on it would
    // restart the gesture coroutine and drop taps in flight.
    val latestOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val isTappable = isDismissible && !isDismissing

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha() }
            .background(color)
            .then(
                if (isTappable) {
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                latestOnDismissRequest()
                            }
                        }
                        .clearAndSetSemantics {
                            contentDescription = dismissLabel
                            onClick(label = dismissLabel) {
                                latestOnDismissRequest()
                                true
                            }
                        }
                } else {
                    Modifier.clearAndSetSemantics {}
                },
            ),
    )
}
