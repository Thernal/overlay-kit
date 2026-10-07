package io.thernal.overlaykit.overlay.impl.presentation.sheet

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
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme
import io.thernal.overlaykit.overlay.impl.presentation.back.modalBackProgress
import io.thernal.overlaykit.overlay.impl.presentation.common.ModalFocusEffect
import io.thernal.overlaykit.overlay.impl.presentation.host.OverlayModalEffect
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayScrim
import io.thernal.overlaykit.overlay.impl.presentation.modal.SyncModalWithStack

@Composable
internal fun BoxScope.BottomSheetHost(
    topKey: Any?,
    topEntry: BottomSheetEntry?,
    state: BottomSheetState,
) {
    SyncModalWithStack(topKey = topKey, topEntry = topEntry, state = state) { entry ->
        state.animationMillis = entry.style.animationMillis
    }
    LaunchedEffect(key1 = state.currentEntry, key2 = state.sheetHeightPx, key3 = state.isDismissing) {
        state.animateInIfReady()
    }

    CloseWhenSwipedAway(state)

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

    val dragging = Modifier.sheetDragging(state = state, entry = entry, isEnabled = isShown)
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
            .then(dragging)
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

/** A settle at Hidden this state did not start is the user closing the sheet by hand. */
@Composable
private fun CloseWhenSwipedAway(state: BottomSheetState) {
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
}

/** Dragging and flinging the sheet, nested scrolls first, when the entry allows it. */
@Composable
private fun Modifier.sheetDragging(
    state: BottomSheetState,
    entry: BottomSheetEntry,
    isEnabled: Boolean,
): Modifier {
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
    if (!entry.isDraggable) {
        return this
    }
    return this
        .nestedScroll(nestedScrollConnection)
        .anchoredDraggable(
            state = state.draggable,
            orientation = Orientation.Vertical,
            enabled = isEnabled,
            flingBehavior = flingBehavior,
        )
}
