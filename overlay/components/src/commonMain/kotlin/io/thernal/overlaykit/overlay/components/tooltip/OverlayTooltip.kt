package io.thernal.overlaykit.overlay.components.tooltip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import io.thernal.overlaykit.overlay.components.common.PublishOverlayEntry
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme
import io.thernal.overlaykit.overlay.core.anchor.overlayAnchor
import io.thernal.overlaykit.overlay.core.anchor.rememberOverlayAnchorId
import io.thernal.overlaykit.overlay.core.placement.OverlayPlacement
import kotlinx.coroutines.delay

/**
 * [content] with a tooltip balloon drawn by [TooltipPlugin]. A tap on [content] toggles it, it
 * hides itself after the style's `autoDismissMillis`, and a tap anywhere closes it.
 *
 * Leave [isVisible] null to let the tooltip keep its own state; pass it, with
 * [onVisibilityChange], to drive it from outside.
 */
@Composable
fun OverlayTooltip(
    tooltipContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean? = null,
    onVisibilityChange: ((Boolean) -> Unit)? = null,
    isToggledByAnchor: Boolean = true,
    placement: OverlayPlacement = OverlayPlacement.Bottom,
    style: TooltipStyle = OverlayTheme.styles.tooltip,
    content: @Composable BoxScope.() -> Unit,
) {
    require(isVisible == null || onVisibilityChange != null) {
        "onVisibilityChange must be provided when isVisible is controlled."
    }

    if (LocalInspectionMode.current) {
        Box(modifier = modifier, content = content)
        return
    }

    val anchorId = rememberOverlayAnchorId(prefix = "tooltip")
    var isOpenInternally by remember { mutableStateOf(false) }
    var showToken by remember { mutableIntStateOf(0) }
    val isShown = isVisible ?: isOpenInternally

    val updateVisibility: (Boolean) -> Unit = { isNextVisible ->
        if (isVisible == null) {
            isOpenInternally = isNextVisible
        }
        onVisibilityChange?.invoke(isNextVisible)
    }

    // The entry stays on the stack while the balloon fades out, so the host can draw the exit;
    // it is hidden once the exit has run.
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
        controller = LocalTooltipController.current,
        isVisible = isShown || isPublished,
        entry = TooltipEntry(
            anchorId = anchorId,
            onDismissRequest = { updateVisibility(false) },
            isVisible = isShown,
            placement = placement,
            style = style,
            showToken = showToken,
            content = tooltipContent,
        ),
    )

    Box(
        modifier = modifier
            .overlayAnchor(anchorId)
            .then(
                if (isToggledByAnchor) {
                    Modifier.clickable {
                        val isNextVisible = !isShown
                        if (isNextVisible) {
                            showToken += 1
                        }
                        updateVisibility(isNextVisible)
                    }
                } else {
                    Modifier
                },
            ),
        content = content,
    )
}
