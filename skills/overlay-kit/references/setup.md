# Setup

## Modules

`overlay/api` (the overlays' composables, styles, `SnackbarManager` — each overlay a thin call to a
`…Renderer` in a CompositionLocal), `overlay/impl` (the renderers, the host, placement, the modal
lifecycle), `overlay/wiring` (Metro: the renderers into the graph's `Set<ProvidedValue<*>>`),
`overlay/testing` (test doubles). A feature module depends on `api` only; the app module on `wiring`.

The modules use Compose runtime, foundation, ui, animation and
`org.jetbrains.androidx.navigationevent:navigationevent-compose` (1.1.0 with Compose Multiplatform
1.12.0). No `api(...)`: a module that uses a type declares its dependency.

## The root

```kotlin
CompositionLocalProvider(*graph.compositionLocals.toTypedArray()) {   // OverlayProvidersModule's renderers
    AppTheme {
        OverlayTheme(styles = appOverlayStyles()) {
            OverlayHost { AppContent() }             // extraPlugins = listOf(…) for the app's own overlays
        }
    }
}
```

Without Metro: `CompositionLocalProvider(*overlayRenderers().toTypedArray())` (`impl`).

Every overlay of the kit is installed, in the order that stacks them right (the snackbar on top). When
app-level providers must read the controllers (a ViewModel routing messages to `LocalSnackbarManager`)
and overlay content must see providers installed after them, split the host:

```kotlin
ProvideOverlays {
    AppProviders {
        OverlayLayers { AppContent() }
    }
}
```

## Styles — from the app's design system

Overlays look like the app's design system, never like the kit. One app-owned file in the design-system
module maps its tokens: `designsystem/…/overlay/AppOverlayStyles.kt`,
`@Composable fun appOverlayStyles(): OverlayStyles` reading the design system's theme —
`OverlayStyles(dialog = DialogStyle(…), bottomSheet = …, dropdown = …, tooltip = …,
snackbar = SnackbarStyle(tones = …, icons = …), showcase = …, strings = OverlayStrings(…))`. The root
installs it inside the design system's theme: `AppTheme { OverlayTheme(styles = appOverlayStyles()) { OverlayHost { … } } }`.

- No mapping yet (overlays look grey-on-white, the kit's neutral defaults): write the file from the
  design system's own tokens — colours, shapes, spacing, typography, motion — before styling anything
  else. The kit's `sample/designsystem/…/overlay/SampleOverlayStyles.kt` shows the shape.
- Never change a look by editing the kit's files: a one-off is `style =` at the call site, the rest is
  the mapping. Map looks; leave behaviour (thresholds, auto-dismiss, insets) at the kit's defaults.

## Android

Back reaches the overlays through the activity's dispatcher: the root is in a `ComponentActivity`. For
predictive back, `android:enableOnBackInvokedCallback="true"` on the application in the manifest.

## iOS

Nothing to set up. The edge-swipe back gesture closes the top overlay where Compose's back gesture is
enabled for the view controller.
