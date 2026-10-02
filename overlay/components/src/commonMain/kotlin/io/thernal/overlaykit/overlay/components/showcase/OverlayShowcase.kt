package io.thernal.overlaykit.overlay.components.showcase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.components.common.PublishOverlayEntry
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme
import io.thernal.overlaykit.overlay.core.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.core.anchor.overlayAnchor
import io.thernal.overlaykit.overlay.core.anchor.rememberOverlayAnchorId
import io.thernal.overlaykit.overlay.core.placement.OverlayPlacement
import kotlinx.coroutines.delay

/**
 * Highlights [content]: the screen dims everywhere except a cut-out of [anchorShape] around it, and
 * a balloon with [tooltipContent] points at it. A tap anywhere, or back, calls [onDismissRequest] —
 * persist "already shown" there and set [isVisible] to false. There is no auto-dismiss.
 *
 * Drawn by [ShowcasePlugin]; the balloon is the tooltip's, styled by the showcase style's `balloon`.
 */
@Composable
fun OverlayShowcase(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    tooltipContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    anchorShape: Shape = RectangleShape,
    placement: OverlayPlacement = OverlayPlacement.Bottom,
    style: ShowcaseStyle = OverlayTheme.styles.showcase,
    content: @Composable BoxScope.() -> Unit,
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier, content = content)
        return
    }

    val anchorId = rememberOverlayAnchorId(prefix = "showcase")
    val anchorRegistry = LocalOverlayAnchorRegistry.current
    // Shown only once the anchor has bounds: a cut-out needs somewhere to be.
    val isShown = isVisible && anchorRegistry.getBounds(anchorId) != null

    var isPublished by remember { mutableStateOf(false) }
    LaunchedEffect(isShown) {
        if (isShown) {
            isPublished = true
        } else {
            if (isPublished && style.exitMillis > 0) {
                delay(style.exitMillis.toLong())
            }
            isPublished = false
        }
    }

    PublishOverlayEntry(
        controller = LocalShowcaseController.current,
        isVisible = isShown || isPublished,
        entry = ShowcaseEntry(
            anchorId = anchorId,
            anchorShape = anchorShape,
            isVisible = isShown,
            onDismissRequest = onDismissRequest,
            placement = placement,
            style = style,
            content = tooltipContent,
        ),
    )

    Box(
        modifier = modifier.overlayAnchor(anchorId),
        content = content,
    )
}
