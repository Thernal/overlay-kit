package io.thernal.overlaykit.overlay.wiring

import androidx.compose.runtime.ProvidedValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.Provides
import io.thernal.overlaykit.overlay.impl.presentation.host.overlayRenderers

/**
 * Contributes every overlay renderer as a `ProvidedValue` into the app graph's
 * `Set<ProvidedValue<*>>`, which the application installs once at its root with
 * `CompositionLocalProvider(*values.toTypedArray())` — above `OverlayHost`.
 */
@BindingContainer
@ContributesTo(AppScope::class)
interface OverlayProvidersModule {
    companion object {
        @Provides
        @ElementsIntoSet
        fun provideOverlayRenderers(): Set<ProvidedValue<*>> {
            return overlayRenderers().toSet()
        }
    }
}
