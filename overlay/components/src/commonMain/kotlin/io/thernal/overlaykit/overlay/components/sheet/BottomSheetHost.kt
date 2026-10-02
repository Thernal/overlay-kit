package io.thernal.overlaykit.overlay.components.sheet

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import io.thernal.overlaykit.overlay.components.common.ModalFocusEffect
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme
import io.thernal.overlaykit.overlay.core.back.OverlayBackHandler
import io.thernal.overlaykit.overlay.core.back.OverlayBackProgress
import io.thernal.overlaykit.overlay.core.host.OverlayModalEffect
import io.thernal.overlaykit.overlay.core.modal.OverlayScrim

@Composable
internal fun BoxScope.BottomSheetHost(
    topKey: Any?,
    topEntry: BottomSheetEntry?,
    state: BottomSheetState,
) {
    // Keyed on topKey only — see DialogHost.
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

    LaunchedEffect(key1 = state.currentEntry, key2 = state.sheetHeightPx, key3 = state.isDismissing) {
        state.animateInIfReady()
    }

    LaunchedEffect(state.shouldRender) {
        state.ensureHiddenWhenNotRendering()
    }

    // A settle at Hidden this state did not start is the user closing the sheet by hand.
    LaunchedEffect(state) {
        snapshotFlow { state.draggable.settledValue }
            .collect { value ->
                if (value == SheetValue.Expanded) {
                    state.hasSettledExpanded = true
                } else if (state.hasSettledExpanded && state.shouldRender && !state.isDismissing) {
                    state.onClosedByUser()
                    state.currentEntry?.onDismissRequest?.invoke()
                }
            }
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

    val density = LocalDensity.current
    val latestStyle by rememberUpdatedState(entry.style)
    val nestedScrollConnection = remember(key1 = state, key2 = density) {
        SheetNestedScrollConnection(
            state = state,
            minFlingVelocityPx = { with(density) { SheetMinFlingVelocity.toPx() } },
            dismissThreshold = { latestStyle.dismissThreshold },
        )
    }
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = state.draggable,
        positionalThreshold = { distance -> distance * entry.style.dismissThreshold },
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
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .wrapContentHeight()
            .graphicsLayer {
                val height = size.height
                translationY = state.offsetPx(measuredHeightPx = height) +
                    height * entry.style.backGestureShift * backProgress.value
            }
            .onSizeChanged { size ->
                state.onSheetHeightChanged(size.height.toFloat())
            }
            .then(
                if (entry.isDraggable) {
                    Modifier
                        .nestedScroll(nestedScrollConnection)
                        .anchoredDraggable(
                            state = state.draggable,
                            orientation = Orientation.Vertical,
                            enabled = isShown,
                            flingBehavior = flingBehavior,
                        )
                } else {
                    Modifier
                },
            )
            .semantics {
                paneTitle = strings.bottomSheetPane
                isTraversalGroup = true
            }
            .focusRequester(focusRequester)
            .focusable(),
    ) {
        entry.content()
    }
}
