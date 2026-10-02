package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarPosition
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle

private const val ENTER_FADE_MILLIS = 500
private const val EXIT_SLIDE_MILLIS = 1200
private const val GLOW_MILLIS = 800
private const val GLOW_HEIGHT_FRACTION = 0.22f
private const val GLOW_EDGE_ALPHA = 0.14f
private const val GLOW_MID_ALPHA = 0.05f
private const val SWIPE_DISMISS_VELOCITY_DP = 350f
private val GlowEasing = CubicBezierEasing(a = 0.4f, b = 0f, c = 0.2f, d = 1f)

@Composable
internal fun BoxScope.SnackbarHost(
    controller: SnackbarController,
    style: SnackbarStyle,
) {
    // The last message stays drawn while it slides out, after the controller has let go of it.
    var lastMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
    val current = controller.current
    SideEffect {
        if (current != null) {
            lastMessage = current
        }
    }
    val message = current ?: lastMessage ?: return
    val isShown = controller.isVisible && current != null

    var swipeDistancePx by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val swipeDismissDistancePx = with(density) { style.swipeDismissDistance.toPx() }
    val swipeDismissVelocityPx = SWIPE_DISMISS_VELOCITY_DP * density.density

    LaunchedEffect(isShown) {
        if (!isShown) {
            controller.onInteractionChanged(isInteracting = false)
            isDragging = false
            swipeDistancePx = 0f
        }
    }

    val dragState = rememberDraggableState { delta ->
        if (!isDragging) {
            isDragging = true
            controller.onInteractionChanged(isInteracting = true)
        }
        swipeDistancePx += delta
    }

    val gestures = Modifier
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
            state = dragState,
            onDragStopped = { velocity ->
                val shouldDismiss = when (message.position) {
                    SnackbarPosition.Top ->
                        swipeDistancePx <= -swipeDismissDistancePx || velocity <= -swipeDismissVelocityPx

                    SnackbarPosition.Bottom ->
                        swipeDistancePx >= swipeDismissDistancePx || velocity >= swipeDismissVelocityPx
                }
                if (isDragging) {
                    controller.onInteractionChanged(isInteracting = false)
                }
                isDragging = false
                swipeDistancePx = 0f
                if (shouldDismiss) {
                    controller.dismiss()
                }
            },
        )

    Box(modifier = Modifier.fillMaxSize()) {
        if (style.isGlowShown) {
            SnackbarGlow(
                tone = style.tones.of(message.kind),
                position = message.position,
                isVisible = isShown,
            )
        }

        val slideOffset: (Int) -> Int = { height ->
            when (message.position) {
                SnackbarPosition.Top -> -height
                SnackbarPosition.Bottom -> height
            }
        }
        AnimatedVisibility(
            visible = isShown,
            enter = slideInVertically(
                initialOffsetY = slideOffset,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            ) + fadeIn(animationSpec = tween(durationMillis = ENTER_FADE_MILLIS)),
            exit = slideOutVertically(
                targetOffsetY = slideOffset,
                animationSpec = tween(durationMillis = EXIT_SLIDE_MILLIS, easing = FastOutLinearInEasing),
            ) + fadeOut(animationSpec = tween(durationMillis = ENTER_FADE_MILLIS)),
            modifier = gestures
                .fillMaxWidth()
                .align(
                    when (message.position) {
                        SnackbarPosition.Top -> Alignment.TopCenter
                        SnackbarPosition.Bottom -> Alignment.BottomCenter
                    },
                ),
        ) {
            Box(
                modifier = Modifier
                    .then(
                        when (message.position) {
                            SnackbarPosition.Top -> Modifier.statusBarsPadding()
                            SnackbarPosition.Bottom -> Modifier.navigationBarsPadding()
                        },
                    )
                    .padding(style.outerPadding)
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                    },
            ) {
                val onAction = {
                    message.onAction?.invoke()
                    controller.dismiss()
                }
                val custom = style.content
                if (custom != null) {
                    custom.Content(message = message, onAction = onAction)
                } else {
                    DefaultSnackbar(message = message, style = style, onAction = onAction)
                }
            }
        }
    }
}

@Composable
private fun BoxScope.SnackbarGlow(
    tone: Color,
    position: SnackbarPosition,
    isVisible: Boolean,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = GLOW_MILLIS, easing = GlowEasing)),
        exit = fadeOut(animationSpec = tween(durationMillis = GLOW_MILLIS, easing = GlowEasing)),
        modifier = Modifier.matchParentSize(),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val glowHeight = size.height * GLOW_HEIGHT_FRACTION
            val startY = when (position) {
                SnackbarPosition.Top -> 0f
                SnackbarPosition.Bottom -> size.height
            }
            val endY = when (position) {
                SnackbarPosition.Top -> glowHeight
                SnackbarPosition.Bottom -> size.height - glowHeight
            }
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        tone.copy(alpha = GLOW_EDGE_ALPHA),
                        tone.copy(alpha = GLOW_MID_ALPHA),
                        Color.Transparent,
                    ),
                    startY = startY,
                    endY = endY,
                ),
            )
        }
    }
}
