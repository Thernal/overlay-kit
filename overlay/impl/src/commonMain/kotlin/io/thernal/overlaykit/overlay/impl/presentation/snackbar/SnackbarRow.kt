package io.thernal.overlaykit.overlay.impl.presentation.snackbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import io.thernal.overlaykit.overlay.api.presentation.snackbar.SnackbarStyle

private const val ICON_BACKGROUND_ALPHA = 0.14f

/** The kit's snackbar row: a tone stripe, the kind's icon if the style has one, the text, the action. */
@Composable
internal fun DefaultSnackbar(
    message: SnackbarMessage,
    style: SnackbarStyle,
    onAction: () -> Unit,
) {
    val tone = style.tones.of(message.kind)
    val icon = style.icons.of(message.kind)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(style.itemSpacing),
        modifier = Modifier
            .fillMaxWidth()
            .clip(style.shape)
            .background(style.containerColor)
            .border(width = style.borderWidth, color = style.borderColor, shape = style.shape)
            .drawBehind {
                drawRect(
                    color = tone,
                    size = Size(width = style.stripeWidth.toPx(), height = size.height),
                )
            }
            .padding(style.contentPadding),
    ) {
        if (icon != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(style.iconContainerSize)
                    .background(color = tone.copy(alpha = ICON_BACKGROUND_ALPHA), shape = style.iconShape),
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(tone),
                    modifier = Modifier.size(style.iconSize),
                )
            }
        }

        BasicText(
            text = message.text,
            style = style.textStyle.copy(color = style.contentColor),
            modifier = Modifier.weight(1f),
        )

        message.actionLabel?.let { label ->
            BasicText(
                text = label,
                style = style.actionTextStyle.copy(color = tone),
                modifier = Modifier
                    .clip(style.iconShape)
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(4.dp),
            )
        }
    }
}
