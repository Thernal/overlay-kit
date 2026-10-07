package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarPosition
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle

@Composable
internal fun BoxScope.SnackbarHost(
    controller: SnackbarController,
    style: SnackbarStyle,
) {
    val message = rememberShownMessage(controller) ?: return
    val isShown = controller.isVisible && controller.current != null
    val swipe = rememberSnackbarSwipe(controller = controller, isShown = isShown, style = style)
    Box(modifier = Modifier.fillMaxSize()) {
        if (style.isGlowShown) {
            SnackbarGlow(
                tone = style.tones.of(message.kind),
                position = message.position,
                isVisible = isShown,
                style = style.glow,
            )
        }
        AnimatedVisibility(
            visible = isShown,
            enter = snackbarEnter(position = message.position, style = style),
            exit = snackbarExit(position = message.position, style = style),
            modifier = Modifier
                .snackbarGestures(
                    isShown = isShown,
                    controller = controller,
                    swipe = swipe,
                    position = message.position,
                )
                .fillMaxWidth()
                .align(message.position.alignment()),
        ) {
            SnackbarFrame(message = message, style = style, controller = controller)
        }
    }
}

/** The current message, or the last one while it slides out after the controller has let go of it. */
@Composable
private fun rememberShownMessage(controller: SnackbarController): SnackbarMessage? {
    var lastMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
    val current = controller.current
    SideEffect {
        if (current != null) {
            lastMessage = current
        }
    }
    return current ?: lastMessage
}

private fun snackbarEnter(
    position: SnackbarPosition,
    style: SnackbarStyle,
): EnterTransition {
    return slideInVertically(
        initialOffsetY = position.slideOffset(),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    ) + fadeIn(animationSpec = tween(durationMillis = style.fadeMillis))
}

private fun snackbarExit(
    position: SnackbarPosition,
    style: SnackbarStyle,
): ExitTransition {
    return slideOutVertically(
        targetOffsetY = position.slideOffset(),
        animationSpec = tween(durationMillis = style.exitSlideMillis, easing = FastOutLinearInEasing),
    ) + fadeOut(animationSpec = tween(durationMillis = style.fadeMillis))
}

/** Off the edge the message comes from. */
private fun SnackbarPosition.slideOffset(): (Int) -> Int {
    return { height ->
        when (this) {
            SnackbarPosition.Top -> -height
            SnackbarPosition.Bottom -> height
        }
    }
}

private fun SnackbarPosition.alignment(): Alignment {
    return when (this) {
        SnackbarPosition.Top -> Alignment.TopCenter
        SnackbarPosition.Bottom -> Alignment.BottomCenter
    }
}
