package io.thernal.overlaykit.overlay.impl.presentation.host

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.semantics.clearAndSetSemantics
import io.thernal.overlaykit.overlay.api.presentation.backdrop.OverlayBackdropStyle
import io.thernal.overlaykit.overlay.impl.presentation.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.impl.presentation.backdrop.LocalOverlayBackdropController
import io.thernal.overlaykit.overlay.impl.presentation.backdrop.backdropTransform
import kotlinx.coroutines.flow.drop

/**
 * Draws the content and, above it, every plugin's overlay layer. While a modal overlay is open the
 * content is hidden from screen readers, keyboard focus cannot travel into it, and what had focus
 * there gets it back when the modal closes.
 */
@Composable
fun OverlayPluginLayers(
    modifier: Modifier = Modifier,
    backdrop: OverlayBackdropStyle = OverlayBackdropStyle(),
    content: @Composable () -> Unit,
) {
    val plugins = LocalOverlayPlugins.current
    val backdropController = LocalOverlayBackdropController.current
    val anchorRegistry = LocalOverlayAnchorRegistry.current
    val modalController = LocalOverlayModalController.current
    val backgroundFocus = rememberBackgroundFocus(modalController)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onLayoutRectChanged(
                throttleMillis = 0,
                debounceMillis = 0,
            ) { bounds ->
                anchorRegistry.hostOriginInRoot = bounds.positionInRoot
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .backdropTransform(progress = backdropController.progress, style = backdrop)
                .behindModals(modalController = modalController, focus = backgroundFocus)
                .observePresses(LocalOverlayBackgroundTouches.current),
        ) {
            content()
        }

        plugins.forEach { plugin ->
            key(plugin.key) {
                plugin.run { Render() }
            }
        }
    }
}

/**
 * The focus of what lies behind the overlays: saved when a modal opens, given back when the last
 * one closes.
 */
@Composable
private fun rememberBackgroundFocus(modalController: OverlayModalController): FocusRequester {
    val backgroundFocus = remember { FocusRequester() }
    LaunchedEffect(key1 = modalController, key2 = backgroundFocus) {
        modalController.saveBackgroundFocus = { backgroundFocus.saveFocusedChild() }
        modalController.restoreBackgroundFocus = { backgroundFocus.restoreFocusedChild() }
        snapshotFlow { modalController.isModalOpen }
            .drop(1)
            .collect { isOpen ->
                if (!isOpen) {
                    modalController.onAllModalsClosed()
                }
            }
    }
    return backgroundFocus
}

/** While a modal is open: hidden from screen readers, and keyboard focus cannot travel in. */
private fun Modifier.behindModals(
    modalController: OverlayModalController,
    focus: FocusRequester,
): Modifier {
    val semantics = if (modalController.isModalOpen) {
        Modifier.clearAndSetSemantics {}
    } else {
        Modifier
    }
    return this
        .then(semantics)
        .focusRequester(focus)
        .focusProperties {
            onEnter = {
                if (modalController.isModalOpen) {
                    cancelFocusChange()
                }
            }
        }
        .focusGroup()
}

/**
 * Reports every press on the content to [touches] — observed on the initial pass and never
 * consumed, so the press still reaches what it landed on.
 */
private fun Modifier.observePresses(touches: OverlayBackgroundTouches): Modifier {
    return pointerInput(touches) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                if (event.type == PointerEventType.Press) {
                    event.changes
                        .filter { change -> change.changedToDown() }
                        .forEach { change -> touches.dispatchPress(change.position) }
                }
            }
        }
    }
}
