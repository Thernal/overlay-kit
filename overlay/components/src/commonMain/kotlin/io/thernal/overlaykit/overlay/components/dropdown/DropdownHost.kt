package io.thernal.overlaykit.overlay.components.dropdown

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.round
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme
import io.thernal.overlaykit.overlay.core.anchor.LocalOverlayAnchorRegistry
import io.thernal.overlaykit.overlay.core.back.OverlayBackHandler
import io.thernal.overlaykit.overlay.core.host.OverlayBackgroundPressEffect
import io.thernal.overlaykit.overlay.core.placement.AnchoredOverlay
import io.thernal.overlaykit.overlay.core.placement.AnchoredOverlayAnimation

@Composable
internal fun DropdownHost(
    topKey: Any?,
    entry: DropdownEntry?,
) {
    if (topKey == null || entry == null) {
        return
    }

    val anchorRegistry = LocalOverlayAnchorRegistry.current
    val strings = OverlayTheme.styles.strings
    val enter = remember(topKey) { Animatable(initialValue = 0f) }
    LaunchedEffect(enter) {
        enter.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = entry.style.animationMillis, easing = FastOutSlowInEasing),
        )
    }
    val animation = remember(key1 = enter, key2 = entry.style.enterScale) {
        AnchoredOverlayAnimation(
            alpha = { enter.value },
            scale = { entry.style.enterScale + (1f - entry.style.enterScale) * enter.value },
        )
    }

    OverlayBackHandler(onBack = entry.onDismissRequest)
    // A press on the anchor is left to the anchor, which toggles the menu itself: closing here too
    // would reopen it on the same press.
    OverlayBackgroundPressEffect(isEnabled = entry.isDismissibleOutside) { position ->
        val anchorBounds = anchorRegistry.getBounds(entry.anchorId)
        if (anchorBounds == null || !anchorBounds.contains(position.round())) {
            entry.onDismissRequest()
        }
    }

    AnchoredOverlay(
        anchorId = entry.anchorId,
        preferredPlacement = entry.placement,
        edgeMargin = entry.style.edgeMargin,
        anchorSpacing = entry.style.anchorSpacing,
        modifier = Modifier.fillMaxSize(),
        animation = animation,
        isWidthMatchingAnchor = entry.isWidthMatchingAnchor,
    ) {
        Column(
            modifier = Modifier
                .clip(entry.style.shape)
                .background(color = entry.style.containerColor, shape = entry.style.shape)
                .border(width = entry.style.borderWidth, color = entry.style.borderColor, shape = entry.style.shape)
                .semantics {
                    paneTitle = strings.menuPane
                    isTraversalGroup = true
                },
            content = entry.menuContent,
        )
    }
}
