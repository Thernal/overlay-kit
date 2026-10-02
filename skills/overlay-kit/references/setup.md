# Setup

## Modules

`overlay/core` (host, placement, modal lifecycle), `overlay/components` (the six overlays, styles,
snackbar manager), `overlay/testing` (test doubles). A feature module that shows overlays depends on
`components`, and on `core` only for `OverlayPlacement` or its own plugins. Tests use `testing`.

The modules use Compose runtime, foundation, ui, animation and
`org.jetbrains.androidx.navigationevent:navigationevent-compose` (1.1.0 with Compose Multiplatform
1.12.0). No `api(...)`: a module that uses a type declares its dependency.

## The root

```kotlin
AppTheme {
    OverlayTheme(styles = appOverlayStyles()) {
        OverlayHost { AppContent() }                 // extraPlugins = listOf(…) for the app's own overlays
    }
}
```

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

## Styles

One function maps the app's tokens: `OverlayStyles(dialog = DialogStyle(…), bottomSheet = …, dropdown = …,
tooltip = …, snackbar = SnackbarStyle(tones = …, icons = …), showcase = …, strings = OverlayStrings(…))`.
Every field has a neutral default; set what the design system defines.

## Android

Back reaches the overlays through the activity's dispatcher: the root is in a `ComponentActivity`. For
predictive back, `android:enableOnBackInvokedCallback="true"` on the application in the manifest.

## iOS

Nothing to set up. The edge-swipe back gesture closes the top overlay where Compose's back gesture is
enabled for the view controller.
