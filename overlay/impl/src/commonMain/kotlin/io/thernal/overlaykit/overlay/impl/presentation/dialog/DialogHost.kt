package io.thernal.overlaykit.overlay.impl.presentation.dialog

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import io.thernal.overlaykit.overlay.api.presentation.dialog.DialogStyle
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme
import io.thernal.overlaykit.overlay.impl.presentation.back.modalBackProgress
import io.thernal.overlaykit.overlay.impl.presentation.common.ModalFocusEffect
import io.thernal.overlaykit.overlay.impl.presentation.common.rememberEnterArmed
import io.thernal.overlaykit.overlay.impl.presentation.host.OverlayModalEffect
import io.thernal.overlaykit.overlay.impl.presentation.modal.AnimatedModalOverlayState
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayScrim
import io.thernal.overlaykit.overlay.impl.presentation.modal.SyncModalWithStack

@Composable
internal fun BoxScope.DialogHost(
    topKey: Any?,
    topEntry: DialogEntry?,
    state: AnimatedModalOverlayState<DialogEntry>,
) {
    SyncModalWithStack(topKey = topKey, topEntry = topEntry, state = state) { entry ->
        state.animationMillis = entry.style.animationMillis
    }
    LaunchedEffect(key1 = state.currentEntry, key2 = state.isDismissing) {
        state.animateInIfReady()
    }

    val entry = state.currentEntry
    if (!state.shouldRender || entry == null) {
        return
    }

    val isShown = !state.isDismissing
    OverlayModalEffect(owner = state, isActive = isShown)
    val backProgress = modalBackProgress(
        isShown = isShown,
        onBack = entry.onDismissRequest,
        isEnabled = entry.isDismissibleByBack,
    )

    val focusRequester = remember { FocusRequester() }
    ModalFocusEffect(entryKey = topKey, isShown = isShown, focusRequester = focusRequester)

    // Called on every composition, shown or not, so the armed state it remembers survives the exit.
    val isEnterArmed = rememberEnterArmed(topKey)
    val animatedScale by rememberDialogScale(entry = entry, isShown = isShown && isEnterArmed)
    val strings = OverlayTheme.styles.strings

    OverlayScrim(
        color = entry.style.scrimColor,
        alpha = { state.progress },
        isDismissing = state.isDismissing,
        onDismissRequest = entry.onDismissRequest,
        dismissLabel = strings.dismiss,
        isDismissible = entry.isDismissibleOutside,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = state.progress
            },
        contentAlignment = entry.alignment,
    ) {
        Box(
            modifier = Modifier
                .systemBarsPadding(style = entry.style)
                .graphicsLayer {
                    val backScale = 1f - (1f - entry.style.backGestureScale) * backProgress.value
                    scaleX = animatedScale * backScale
                    scaleY = animatedScale * backScale
                }
                // Taps inside the dialog must not reach the scrim behind it.
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {})
                }
                .semantics {
                    paneTitle = strings.dialogPane
                    isTraversalGroup = true
                }
                .focusRequester(focusRequester)
                .focusable(),
        ) {
            entry.content()
        }
    }
}

/** Springs up from [DialogStyle.enterScale] once the dialog is [isShown], and shrinks back as it leaves. */
@Composable
private fun rememberDialogScale(
    entry: DialogEntry,
    isShown: Boolean,
): State<Float> {
    return animateFloatAsState(
        targetValue = if (isShown) {
            1f
        } else {
            entry.style.enterScale
        },
        animationSpec = if (isShown) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            )
        } else {
            tween(durationMillis = entry.style.animationMillis, easing = FastOutLinearInEasing)
        },
        label = "dialog_scale",
    )
}

private fun Modifier.systemBarsPadding(style: DialogStyle): Modifier {
    val status = if (style.isStatusBarPadded) {
        Modifier.statusBarsPadding()
    } else {
        Modifier
    }
    val navigation = if (style.isNavigationBarPadded) {
        Modifier.navigationBarsPadding()
    } else {
        Modifier
    }
    return this.then(status).then(navigation)
}
