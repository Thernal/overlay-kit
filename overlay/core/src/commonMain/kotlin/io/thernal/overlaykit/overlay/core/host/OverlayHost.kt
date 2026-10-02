package io.thernal.overlaykit.overlay.core.host

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
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
import io.thernal.overlaykit.overlay.core.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.core.anchor.OverlayAnchorRegistry
import io.thernal.overlaykit.overlay.core.backdrop.LocalOverlayBackdropController
import io.thernal.overlaykit.overlay.core.backdrop.OverlayBackdropController
import io.thernal.overlaykit.overlay.core.backdrop.OverlayBackdropStyle
import io.thernal.overlaykit.overlay.core.backdrop.backdropTransform
import kotlinx.coroutines.flow.drop

private val LocalOverlayPlugins = staticCompositionLocalOf<List<OverlayLayerPlugin>> {
    error("No overlay plugins: OverlayLayers must sit inside ProvideOverlayControllers.")
}

/**
 * Draws [plugins]' overlays above [content] — one call at the root of the app.
 *
 * It is [ProvideOverlayControllers] and [OverlayLayers] together. Split them when other app-level
 * providers sit between the two: the controllers must be installed before anything that reads them
 * (a ViewModel routing messages to the snackbar), and the layers must come after anything overlay
 * content needs — overlay content is composed at the layers' position, so a CompositionLocal
 * provided below them is invisible to it.
 */
@Composable
fun OverlayHost(
    plugins: List<OverlayLayerPlugin>,
    modifier: Modifier = Modifier,
    backdrop: OverlayBackdropStyle = OverlayBackdropStyle(),
    content: @Composable () -> Unit,
) {
    ProvideOverlayControllers(plugins = plugins) {
        OverlayLayers(
            modifier = modifier,
            backdrop = backdrop,
            content = content,
        )
    }
}

/**
 * Installs the overlay controllers — the backdrop, the anchor registry, the modal tracker and each
 * plugin's own `Provide` — as CompositionLocals. It draws nothing; [OverlayLayers] does.
 */
@Composable
fun ProvideOverlayControllers(
    plugins: List<OverlayLayerPlugin>,
    content: @Composable () -> Unit,
) {
    val resolvedPlugins = remember(plugins) { validatePlugins(plugins) }
    val backdropController = remember { OverlayBackdropController() }
    val anchorRegistry = remember { OverlayAnchorRegistry() }
    val modalController = remember { OverlayModalController() }
    val backgroundTouches = remember { OverlayBackgroundTouches() }

    CompositionLocalProvider(
        LocalOverlayBackdropController provides backdropController,
        LocalOverlayAnchorRegistry provides anchorRegistry,
        LocalOverlayModalController provides modalController,
        LocalOverlayBackgroundTouches provides backgroundTouches,
        LocalOverlayPlugins provides resolvedPlugins,
    ) {
        ProvidePlugins(
            plugins = resolvedPlugins,
            index = 0,
            content = content,
        )
    }
}

/**
 * Draws the content and, above it, every plugin's overlay layer. While a modal overlay is open the
 * content is hidden from screen readers, keyboard focus cannot travel into it, and what had focus
 * there gets it back when the modal closes.
 */
@Composable
fun OverlayLayers(
    modifier: Modifier = Modifier,
    backdrop: OverlayBackdropStyle = OverlayBackdropStyle(),
    content: @Composable () -> Unit,
) {
    val plugins = LocalOverlayPlugins.current
    val backdropController = LocalOverlayBackdropController.current
    val anchorRegistry = LocalOverlayAnchorRegistry.current
    val modalController = LocalOverlayModalController.current
    val backgroundTouches = LocalOverlayBackgroundTouches.current
    val backgroundFocus = remember { FocusRequester() }
    val isModalOpen = modalController.isModalOpen

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
                .then(
                    if (isModalOpen) {
                        Modifier.clearAndSetSemantics {}
                    } else {
                        Modifier
                    },
                )
                .focusRequester(backgroundFocus)
                .focusProperties {
                    onEnter = {
                        if (modalController.isModalOpen) {
                            cancelFocusChange()
                        }
                    }
                }
                .focusGroup()
                .pointerInput(backgroundTouches) {
                    // Observed on the initial pass and never consumed: the press still reaches
                    // what it landed on.
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                            if (event.type == PointerEventType.Press) {
                                event.changes
                                    .filter { change -> change.changedToDown() }
                                    .forEach { change -> backgroundTouches.dispatchPress(change.position) }
                            }
                        }
                    }
                },
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

@Composable
private fun ProvidePlugins(
    plugins: List<OverlayLayerPlugin>,
    index: Int,
    content: @Composable () -> Unit,
) {
    val head = plugins.getOrNull(index)
    if (head == null) {
        content()
        return
    }

    key(head.key) {
        head.Provide {
            ProvidePlugins(
                plugins = plugins,
                index = index + 1,
                content = content,
            )
        }
    }
}

private fun validatePlugins(plugins: List<OverlayLayerPlugin>): List<OverlayLayerPlugin> {
    plugins.forEach { plugin ->
        check(plugin.key.isNotBlank()) {
            "Overlay plugin key cannot be blank."
        }
    }

    val duplicateKeys = plugins
        .groupBy { plugin -> plugin.key }
        .filterValues { samePlugins -> samePlugins.size > 1 }
        .keys
        .sorted()
    check(duplicateKeys.isEmpty()) {
        "Duplicate overlay plugin key(s): ${duplicateKeys.joinToString()}. " +
            "Each OverlayLayerPlugin key must be unique."
    }

    return plugins.toList()
}
