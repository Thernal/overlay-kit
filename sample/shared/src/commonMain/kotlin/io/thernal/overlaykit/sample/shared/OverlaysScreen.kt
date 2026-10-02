package io.thernal.overlaykit.sample.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.api.presentation.dialog.OverlayDialog
import io.thernal.overlaykit.overlay.api.presentation.dropdown.OverlayDropdown
import io.thernal.overlaykit.overlay.api.presentation.sheet.OverlayBottomSheet
import io.thernal.overlaykit.overlay.api.presentation.showcase.OverlayShowcase
import io.thernal.overlaykit.overlay.api.presentation.snackbar.LocalSnackbarManager
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarKind
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarMessage
import io.thernal.overlaykit.overlay.api.domain.snackbar.SnackbarPosition
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme
import io.thernal.overlaykit.overlay.api.presentation.tooltip.OverlayTooltip

private const val SHEET_ITEM_COUNT = 30

/** One section per overlay. Every overlay's content is ordinary composables — Material, here. */
@Composable
internal fun OverlaysScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DialogSection()
        HorizontalDivider()
        SheetSection()
        HorizontalDivider()
        DropdownSection()
        HorizontalDivider()
        TooltipSection()
        HorizontalDivider()
        SnackbarSection()
        HorizontalDivider()
        ShowcaseSection()
        HorizontalDivider()
        FocusSection()
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

/** The text colour that reads on the current overlay surface. */
@Composable
private fun overlayTextColor(): Color {
    val surface = OverlayTheme.styles.dialog.containerColor
    return if (surface.luminance() < HALF) {
        Color.White
    } else {
        Color.Black
    }
}

private const val HALF = 0.5f

private fun Color.luminance(): Float {
    return red * LUMA_RED + green * LUMA_GREEN + blue * LUMA_BLUE
}

private const val LUMA_RED = 0.2126f
private const val LUMA_GREEN = 0.7152f
private const val LUMA_BLUE = 0.0722f

@Composable
private fun DialogSection() {
    var isFirstVisible by remember { mutableStateOf(false) }
    var isSecondVisible by remember { mutableStateOf(false) }

    SectionTitle(text = "Dialog")
    Button(onClick = { isFirstVisible = true }) {
        Text(text = "Open dialog")
    }

    OverlayDialog(isVisible = isFirstVisible, onDismissRequest = { isFirstVisible = false }) {
        Text(text = "A dialog", style = MaterialTheme.typography.titleLarge, color = overlayTextColor())
        Text(
            text = "Tap the scrim or press back to close it. A second dialog stacks on top.",
            color = overlayTextColor(),
            modifier = Modifier.padding(vertical = 12.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { isSecondVisible = true }) {
                Text(text = "Open another")
            }
            TextButton(onClick = { isFirstVisible = false }) {
                Text(text = "Close")
            }
        }
    }

    OverlayDialog(isVisible = isSecondVisible, onDismissRequest = { isSecondVisible = false }) {
        Text(text = "On top", color = overlayTextColor())
        TextButton(onClick = { isSecondVisible = false }) {
            Text(text = "Back to the first")
        }
    }
}

@Composable
private fun SheetSection() {
    var isShortVisible by remember { mutableStateOf(false) }
    var isListVisible by remember { mutableStateOf(false) }

    SectionTitle(text = "Bottom sheet")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { isShortVisible = true }) {
            Text(text = "Short sheet")
        }
        Button(onClick = { isListVisible = true }) {
            Text(text = "Sheet with a list")
        }
    }

    OverlayBottomSheet(isVisible = isShortVisible, onDismissRequest = { isShortVisible = false }) {
        Text(
            text = "Drag me down, tap the scrim or press back.",
            color = overlayTextColor(),
            modifier = Modifier.padding(vertical = 24.dp),
        )
    }

    OverlayBottomSheet(isVisible = isListVisible, onDismissRequest = { isListVisible = false }) {
        Text(
            text = "Scroll the list to its top, then keep pulling: the sheet follows.",
            color = overlayTextColor(),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp)) {
            items(count = SHEET_ITEM_COUNT) { index ->
                Text(
                    text = "Item ${index + 1}",
                    color = overlayTextColor(),
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun DropdownSection() {
    var isExpanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("Apples") }

    SectionTitle(text = "Dropdown")
    OverlayDropdown(
        isExpanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = Modifier.fillMaxWidth(),
        // The button inside toggles the menu itself; the anchor's own toggle would undo it.
        isToggledByAnchor = false,
        menuContent = {
            listOf("Apples", "Pears", "Plums", "Quinces").forEach { fruit ->
                TextButton(
                    onClick = {
                        selected = fruit
                        isExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = fruit)
                }
            }
        },
    ) {
        OutlinedButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Fruit: $selected")
        }
    }
}

@Composable
private fun TooltipSection() {
    SectionTitle(text = "Tooltip")
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        OverlayTooltip(
            tooltipContent = { Text(text = "Placed below, flips when there is no room", color = Color.White) },
        ) {
            Text(text = "Tap me", modifier = Modifier.padding(8.dp))
        }
        Box(modifier = Modifier.weight(1f))
        OverlayTooltip(
            tooltipContent = { Text(text = "Clamped to the screen's edge", color = Color.White) },
        ) {
            Text(text = "And me", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun SnackbarSection() {
    val snackbar = LocalSnackbarManager.current

    SectionTitle(text = "Snackbar")
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SnackbarKind.entries.forEach { kind ->
            OutlinedButton(onClick = { snackbar.show(SnackbarMessage(text = "A ${kind.name} message", kind = kind)) }) {
                Text(text = kind.name)
            }
        }
        Button(
            onClick = {
                snackbar.show(
                    SnackbarMessage(
                        text = "Saved at the bottom",
                        kind = SnackbarKind.Success,
                        position = SnackbarPosition.Bottom,
                        actionLabel = "Undo",
                        onAction = {
                            snackbar.show(SnackbarMessage(text = "Undone"))
                        },
                    ),
                )
            },
        ) {
            Text(text = "Bottom, with action")
        }
    }
}

@Composable
private fun ShowcaseSection() {
    var isShowcaseVisible by remember { mutableStateOf(false) }

    SectionTitle(text = "Showcase")
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = { isShowcaseVisible = true }) {
            Text(text = "Highlight the badge")
        }
        OverlayShowcase(
            isVisible = isShowcaseVisible,
            onDismissRequest = { isShowcaseVisible = false },
            tooltipContent = { Text(text = "New: this badge. Tap anywhere to continue.", color = Color.White) },
            anchorShape = CircleShape,
        ) {
            Text(text = "NEW", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
private fun FocusSection() {
    var text by remember { mutableStateOf("") }
    var isDialogVisible by remember { mutableStateOf(false) }

    SectionTitle(text = "Focus")
    Text(text = "Type here, then open the dialog: the keyboard closes, and focus comes back after.")
    OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
    Button(onClick = { isDialogVisible = true }) {
        Text(text = "Open dialog")
    }
    OverlayDialog(isVisible = isDialogVisible, onDismissRequest = { isDialogVisible = false }) {
        Text(text = "Focus moved here.", color = overlayTextColor())
        TextButton(onClick = { isDialogVisible = false }) {
            Text(text = "Close")
        }
    }
}
