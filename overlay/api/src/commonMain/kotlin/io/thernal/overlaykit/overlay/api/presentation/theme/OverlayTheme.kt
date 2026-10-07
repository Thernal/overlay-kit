package io.thernal.overlaykit.overlay.api.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalOverlayStyles = staticCompositionLocalOf { OverlayStyles() }

/**
 * Installs [styles] for the overlays below. Put it above `OverlayHost`: overlays are drawn at the
 * host, and read the styles there as well as at the call site.
 */
@Composable
fun OverlayTheme(
    styles: OverlayStyles,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOverlayStyles provides styles) {
        content()
    }
}

object OverlayTheme {
    val styles: OverlayStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalOverlayStyles.current
}
