package io.thernal.overlaykit.overlay.api.presentation.dropdown

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.overlaykit.overlay.api.domain.placement.OverlayPlacement
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/**
 * [content] as the anchor of a menu. A tap on the anchor toggles the menu (unless
 * [isToggledByAnchor] is false); a press outside it and back close it, through [onExpandedChange].
 * The menu is as wide as the anchor unless [isWidthMatchingAnchor] is false, and moves to another
 * side of the anchor when [placement] has no room. It is not modal: the screen keeps working.
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
    LocalDropdownRenderer.current.Render(
        params = DropdownParams(
            isExpanded = isExpanded,
            onExpandedChange = onExpandedChange,
            isEnabled = isEnabled,
            placement = placement,
            isWidthMatchingAnchor = isWidthMatchingAnchor,
            isToggledByAnchor = isToggledByAnchor,
            isDismissibleOutside = isDismissibleOutside,
            style = style,
        ),
        menuContent = menuContent,
        modifier = modifier,
        content = content,
    )
}
