package io.thernal.overlaykit.overlay.api.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** The dialog's card as the kit draws it: [content] on the style's container, border and padding. */
@Composable
fun DialogSurface(
    style: DialogStyle,
    horizontalAlignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(color = style.containerColor, shape = style.shape)
            .border(width = style.borderWidth, color = style.borderColor, shape = style.shape)
            .padding(style.contentPadding),
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}
