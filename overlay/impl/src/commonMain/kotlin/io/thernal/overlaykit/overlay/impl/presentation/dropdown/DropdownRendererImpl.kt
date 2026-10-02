package io.thernal.overlaykit.overlay.impl.presentation.dropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownParams
import io.thernal.overlaykit.overlay.api.presentation.dropdown.DropdownRenderer
import io.thernal.overlaykit.overlay.impl.presentation.anchor.overlayAnchor
import io.thernal.overlaykit.overlay.impl.presentation.anchor.rememberOverlayAnchorId
import io.thernal.overlaykit.overlay.impl.presentation.common.PublishOverlayEntry

/** Registers the anchor and publishes the menu to [DropdownPlugin], which places it beside the anchor. */
class DropdownRendererImpl : DropdownRenderer {
    @Composable
    override fun Render(
        params: DropdownParams,
        menuContent: @Composable ColumnScope.() -> Unit,
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit,
    ) {
        if (LocalInspectionMode.current) {
            Box(modifier = modifier, content = content)
            return
        }

        val anchorId = rememberOverlayAnchorId(prefix = "dropdown")

        PublishOverlayEntry(
            controller = LocalDropdownController.current,
            isVisible = params.isExpanded && params.isEnabled,
            entry = DropdownEntry(
                anchorId = anchorId,
                onDismissRequest = { params.onExpandedChange(false) },
                placement = params.placement,
                isWidthMatchingAnchor = params.isWidthMatchingAnchor,
                isDismissibleOutside = params.isDismissibleOutside,
                style = params.style,
                menuContent = menuContent,
            ),
        )

        Box(
            modifier = modifier
                .overlayAnchor(anchorId)
                .then(
                    if (params.isToggledByAnchor) {
                        Modifier.clickable(enabled = params.isEnabled, role = Role.DropdownList) {
                            params.onExpandedChange(!params.isExpanded)
                        }
                    } else {
                        Modifier
                    },
                ),
            content = content,
        )
    }
}
