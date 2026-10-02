package io.thernal.overlaykit.sample.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.thernal.overlaykit.overlay.components.dialog.DialogPlugin
import io.thernal.overlaykit.overlay.components.dropdown.DropdownPlugin
import io.thernal.overlaykit.overlay.components.sheet.BottomSheetPlugin
import io.thernal.overlaykit.overlay.components.showcase.ShowcasePlugin
import io.thernal.overlaykit.overlay.components.snackbar.SnackbarPlugin
import io.thernal.overlaykit.overlay.components.theme.OverlayStyles
import io.thernal.overlaykit.overlay.components.theme.OverlayTheme
import io.thernal.overlaykit.overlay.components.tooltip.TooltipPlugin
import io.thernal.overlaykit.overlay.core.host.OverlayHost

/**
 * The sample's root, as an application's root would be: the overlay theme, then the host with
 * every plugin, then the screen. Material is the sample's own UI kit; the overlays do not use it.
 */
@Composable
fun SampleApp() {
    var isBranded by remember { mutableStateOf(false) }
    val plugins = remember {
        listOf(
            BottomSheetPlugin(),
            DialogPlugin(),
            DropdownPlugin(),
            ShowcasePlugin(),
            TooltipPlugin(),
            SnackbarPlugin(),
        )
    }

    MaterialTheme {
        OverlayTheme(
            styles = if (isBranded) {
                brandedOverlayStyles()
            } else {
                OverlayStyles()
            },
        ) {
            OverlayHost(plugins = plugins) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .safeDrawingPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(text = "overlay-kit", style = MaterialTheme.typography.headlineSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "App tokens mapped through OverlayStyles")
                            Switch(checked = isBranded, onCheckedChange = { isBranded = it })
                        }
                        OverlaysScreen()
                    }
                }
            }
        }
    }
}
