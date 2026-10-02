# overlay/core — building on the host

What the six overlays are made of, for an app that needs a seventh. Using the six:
[`../components/README.md`](../components/README.md).

## A plugin

```kotlin
class BannerPlugin : OverlayLayerPlugin {
    override val key = "banner"                       // unique among the host's plugins

    @Composable
    override fun Provide(content: @Composable () -> Unit) {
        val controller = remember { OverlayStackEntryController<BannerEntry>() }
        CompositionLocalProvider(LocalBannerController provides controller, content = content)
    }

    @Composable
    override fun BoxScope.Render() {
        val controller = LocalBannerController.current
        val entry by remember(controller) { derivedStateOf { controller.topEntry } }
        entry?.let { BannerHost(it) }                 // drawn above the app's content
    }
}
```

The producer side, in a composable the app calls:

```kotlin
val key = remember { Any() }
val controller = LocalBannerController.current
DisposableEffect(key, controller) { onDispose { controller.remove(key) } }
SideEffect { controller.update(key = key, entry = BannerEntry(…)) }      // every composition
LaunchedEffect(isVisible) { if (isVisible) controller.show(key) else controller.hide(key) }
```

Key the host's lifecycle on `controller.topKey`, not on the entry: entries carry fresh lambdas on every
producer recomposition.

## A modal overlay

`ModalOverlayState` is the lifecycle — which entry is up, whether it is still drawn while leaving — and
`AnimatedModalOverlayState` moves its progress with an `Animatable`. A host drives it from three places:

```kotlin
LaunchedEffect(topKey) { state.onTopEntryChanged(topKey, latestTopEntry) }
SideEffect { state.syncTopEntry(topKey, topEntry) }
LaunchedEffect(state.currentEntry, state.isDismissing) { state.animateInIfReady() }
```

While it is on screen, a modal overlay also:

- calls `OverlayModalEffect(owner, isActive)` — the content behind is hidden from screen readers and
  from keyboard focus;
- draws `OverlayScrim(color, alpha = { state.progress }, …, dismissLabel)`;
- calls `OverlayBackHandler(onBack)` **only while shown**, and may read the returned progress in a layer
  block to follow a predictive back gesture;
- moves focus in: `if (LocalOverlayModalController.current.takeBackgroundFocus()) requester.requestFocus()`
  on a `focusable()` container; the host gives it back when the last modal closes.

## The backdrop

`OverlayBackdropContribution(owner) { progress }` pushes the content back by `progress` (0–1); the
strongest contribution wins. The host's `OverlayBackdropStyle` says what "back" looks like.

## Anchored placement

```kotlin
val anchorId = rememberOverlayAnchorId(prefix = "banner")
Box(Modifier.overlayAnchor(anchorId)) { … }                       // the anchor, anywhere below the host

// in the plugin's Render:
AnchoredOverlay(
    anchorId = anchorId,
    preferredPlacement = OverlayPlacement.Bottom,
    edgeMargin = 16.dp,
    anchorSpacing = 8.dp,
    modifier = Modifier.fillMaxSize(),
    state = layoutState,                                          // what was resolved, for draw blocks
    animation = AnchoredOverlayAnimation(alpha = { a.value }, scale = { s.value }),
    caretInset = 8.dp,
    chrome = { Spacer(Modifier.drawBehind { /* read layoutState.position here */ }) },
) { BannerBody() }
```

One layout pass measures the body, resolves the position from its real size and places it.
`layoutState.position` — the placement used and the caret offset — is written during layout: read it in
draw and layer blocks only. `anchorContactPoint(position, size)` is the point touching the anchor, for a
transform origin of your own.

## Presses outside

`OverlayBackgroundPressEffect(isEnabled) { offset -> … }` is called for every press on the content, in
host coordinates, without consuming it — for a non-modal overlay that closes on an outside press.
