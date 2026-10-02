package io.thernal.overlaykit.overlay.components.dropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import io.thernal.overlaykit.overlay.components.common.PublishOverlayEntry
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme
import io.thernal.overlaykit.overlay.core.anchor.overlayAnchor
import io.thernal.overlaykit.overlay.core.anchor.rememberOverlayAnchorId
import io.thernal.overlaykit.overlay.core.placement.OverlayPlacement

/**
 * [content] as the anchor of a menu drawn by [DropdownPlugin]. A tap on the anchor toggles the
 * menu (unless [isToggledByAnchor] is false); a press outside it and back close it, through
 * [onExpandedChange]. The menu is as wide as the anchor unless [isWidthMatchingAnchor] is false, and
 * moves to another side of the anchor when [placement] has no room.
 */
@Composable
fun OverlayDropdown(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    placement: OverlayPlacement = OverlayPlacement.Bottom,
    isWidthMatchingAnchor: Boolean = true,
    isToggledByAnchor: Boolean = true,
    isDismissibleOutside: Boolean = true,
    style: DropdownStyle = OverlayTheme.styles.dropdown,
    content: @Composable BoxScope.() -> Unit,
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier, content = content)
        return
    }

    val anchorId = rememberOverlayAnchorId(prefix = "dropdown")

    PublishOverlayEntry(
        controller = LocalDropdownController.current,
        isVisible = isExpanded && isEnabled,
        entry = DropdownEntry(
            anchorId = anchorId,
            onDismissRequest = { onExpandedChange(false) },
            placement = placement,
            isWidthMatchingAnchor = isWidthMatchingAnchor,
            isDismissibleOutside = isDismissibleOutside,
            style = style,
            menuContent = menuContent,
        ),
    )

    Box(
        modifier = modifier
            .overlayAnchor(anchorId)
            .then(
                if (isToggledByAnchor) {
                    Modifier.clickable(enabled = isEnabled, role = Role.DropdownList) {
                        onExpandedChange(!isExpanded)
                    }
                } else {
                    Modifier
                },
            ),
        content = content,
    )
}
