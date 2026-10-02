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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import io.thernal.overlaykit.overlay.impl.presentation.common.ModalFocusEffect
import io.thernal.overlaykit.overlay.impl.presentation.common.rememberEnterArmed
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme
import io.thernal.overlaykit.overlay.impl.presentation.back.OverlayBackHandler
import io.thernal.overlaykit.overlay.impl.presentation.back.OverlayBackProgress
import io.thernal.overlaykit.overlay.impl.presentation.host.OverlayModalEffect
import io.thernal.overlaykit.overlay.impl.presentation.modal.AnimatedModalOverlayState
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayScrim

@Composable
internal fun BoxScope.DialogHost(
    topKey: Any?,
    topEntry: DialogEntry?,
    state: AnimatedModalOverlayState<DialogEntry>,
) {
    // Keyed on topKey only: entries are republished with fresh lambdas on every producer
    // recomposition, and keying on them would restart this lifecycle coroutine each time. Content
    // updates for the key on screen go through syncTopEntry instead.
    val latestTopEntry by rememberUpdatedState(topEntry)
    LaunchedEffect(topKey) {
        latestTopEntry?.let { entry ->
            state.animationMillis = entry.style.animationMillis
        }
        state.onTopEntryChanged(
            topKey = topKey,
            topEntry = latestTopEntry,
        )
    }

    SideEffect {
        state.syncTopEntry(
            topKey = topKey,
            topEntry = topEntry,
        )
    }

    LaunchedEffect(key1 = state.currentEntry, key2 = state.isDismissing) {
        state.animateInIfReady()
    }

    LaunchedEffect(state.shouldRender) {
        state.ensureHiddenWhenNotRendering()
    }

    val entry = state.currentEntry
    if (!state.shouldRender || entry == null) {
        return
    }

    val isShown = !state.isDismissing
    OverlayModalEffect(owner = state, isActive = isShown)
    val backProgress = if (isShown) {
        OverlayBackHandler(
            onBack = entry.onDismissRequest,
            isEnabled = entry.isDismissibleByBack,
        )
    } else {
        OverlayBackProgress.None
    }

    val focusRequester = remember { FocusRequester() }
    ModalFocusEffect(entryKey = topKey, isShown = isShown, focusRequester = focusRequester)

    val isEnterArmed = rememberEnterArmed(topKey)
    val isGrowing = isEnterArmed && isShown
    val animatedScale by animateFloatAsState(
        targetValue = if (isGrowing) {
            1f
        } else {
            entry.style.enterScale
        },
        animationSpec = if (isGrowing) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            )
        } else {
            tween(durationMillis = entry.style.animationMillis, easing = FastOutLinearInEasing)
        },
        label = "dialog_scale",
    )
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
                .then(
                    if (entry.style.isStatusBarPadded) {
                        Modifier.statusBarsPadding()
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (entry.style.isNavigationBarPadded) {
                        Modifier.navigationBarsPadding()
                    } else {
                        Modifier
                    },
                )
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
