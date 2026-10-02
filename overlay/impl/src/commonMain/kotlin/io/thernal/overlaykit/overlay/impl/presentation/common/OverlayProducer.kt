package io.thernal.overlaykit.overlay.impl.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import io.thernal.overlaykit.overlay.impl.presentation.modal.OverlayStackEntryController

/**
 * The producer side every overlay call shares: one key per call site, the latest [entry]
 * republished after every composition, shown or hidden as [isVisible] says, removed when the call
 * leaves the composition.
 */
@Composable
internal fun <E> PublishOverlayEntry(
    controller: OverlayStackEntryController<E>,
    isVisible: Boolean,
    entry: E,
) {
    val entryKey = remember { Any() }

    DisposableEffect(key1 = entryKey, key2 = controller) {
        onDispose {
            controller.remove(entryKey)
        }
    }

    // The entry goes in before the show: a host reading the new top key finds its entry already.
    SideEffect {
        controller.update(
            key = entryKey,
            entry = entry,
        )
    }

    LaunchedEffect(key1 = isVisible, key2 = entryKey, key3 = controller) {
        if (isVisible) {
            controller.show(entryKey)
        } else {
            controller.hide(entryKey)
        }
    }
}
