package io.thernal.overlaykit.overlay.impl.presentation.modal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * Keeps a modal's [state] in step with the top of its stack: [onEntryShown] and the swap when the top
 * changes, the content of the entry on screen on every composition, and a snap to hidden once nothing
 * is left to render.
 *
 * Keyed on [topKey] only: entries are republished with fresh lambdas on every producer recomposition,
 * and keying on them would restart this lifecycle coroutine each time. Content updates for the key on
 * screen go through `syncTopEntry` instead.
 */
@Composable
internal fun <E> SyncModalWithStack(
    topKey: Any?,
    topEntry: E?,
    state: ModalOverlayState<E>,
    onEntryShown: (E) -> Unit,
) {
    val latestTopEntry by rememberUpdatedState(topEntry)
    LaunchedEffect(topKey) {
        latestTopEntry?.let(onEntryShown)
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

    LaunchedEffect(state.shouldRender) {
        state.ensureHiddenWhenNotRendering()
    }
}
