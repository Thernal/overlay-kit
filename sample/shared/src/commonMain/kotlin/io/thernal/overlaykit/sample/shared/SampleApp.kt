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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zacsweers.metro.createGraph
import io.thernal.overlaykit.overlay.api.presentation.host.OverlayHost
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayStyles
import io.thernal.overlaykit.overlay.api.presentation.theme.OverlayTheme

/**
 * The sample's root, as an application's root would be: the graph's renderers, the overlay theme,
 * the host, then the screen. Material is the sample's own UI kit; the overlays do not use it.
 */
@Composable
fun SampleApp() {
    var isBranded by remember { mutableStateOf(false) }
    val graph = remember { createGraph<SampleGraph>() }

    // Once, at the root: the copy the spread makes is not worth avoiding.
    @Suppress("SpreadOperator")
    CompositionLocalProvider(*graph.compositionLocals.toTypedArray()) {
        SampleRoot(isBranded = isBranded, onBrandedChange = { isBranded = it })
    }
}

@Composable
private fun SampleRoot(
    isBranded: Boolean,
    onBrandedChange: (Boolean) -> Unit,
) {
    MaterialTheme {
        OverlayTheme(
            styles = if (isBranded) {
                brandedOverlayStyles()
            } else {
                OverlayStyles()
            },
        ) {
            OverlayHost {
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
                            Switch(checked = isBranded, onCheckedChange = onBrandedChange)
                        }
                        OverlaysScreen()
                    }
                }
            }
        }
    }
}
