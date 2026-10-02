# overlay/api — using the overlays

Task by task. Every overlay is a composable you call where its state lives; a plugin at the root draws
it. Why it is built this way: [`../README.md`](../README.md).

## The root

```kotlin
@Composable
fun AppRoot(graph: AppGraph) {
    // The renderers — OverlayWiring contributes them to the graph's Set<ProvidedValue<*>>.
    CompositionLocalProvider(*graph.compositionLocals.toTypedArray()) {
        AppTheme {                               // anything overlay content reads goes ABOVE the host
            OverlayTheme(styles = appOverlayStyles()) {
                OverlayHost {
                    AppContent()
                }
            }
        }
    }
}
```

Without a DI graph, install `impl`'s `overlayRenderers()` the same way. A feature module depends on
`overlay/api` only.

- `OverlayHost` installs every overlay of the kit, stacked bottom to top: sheets, dialogs, dropdowns,
  showcases, tooltips, then the app's own (`extraPlugins = listOf(BannerPlugin())`), and the snackbar
  last. An overlay used outside a host fails at once with its plugin's name.
- **Overlay content is composed at the host**, not at the call site. A CompositionLocal provided between
  the host and the call site (a screen's own theme, a ViewModel store) is not visible inside the overlay.
  Provide it above the host, or split the host: `ProvideOverlays { …providers… OverlayLayers { content } }`.
- `OverlayHost(backdrop = OverlayBackdropStyle.None)` keeps the content still behind sheets.

## Styling

Every overlay takes a `style`; its default comes from `OverlayTheme.styles`. Map the app's tokens once:

```kotlin
fun appOverlayStyles(tokens: AppTokens) = OverlayStyles(
    dialog = DialogStyle(containerColor = tokens.surface, borderColor = tokens.outline, shape = tokens.shapeLarge),
    bottomSheet = BottomSheetStyle(containerColor = tokens.surface, dragHandleColor = tokens.outline),
    dropdown = DropdownStyle(containerColor = tokens.surfaceRaised),
    tooltip = TooltipStyle(containerColor = tokens.inverseSurface),
    snackbar = SnackbarStyle(
        containerColor = tokens.surface,
        contentColor = tokens.onSurface,
        textStyle = tokens.label,
        tones = SnackbarTones(success = tokens.success, error = tokens.error, warning = tokens.warning, info = tokens.info),
        icons = SnackbarIcons(success = AppIcons.CheckCircle, error = AppIcons.Error),
    ),
    strings = OverlayStrings(dismiss = stringResource(Res.string.dismiss), dialogPane = …),
)
```

`OverlayStrings` are what screen readers hear (the scrim's action, pane titles). English by default —
translate them here. The overlays draw no other text: content is yours.

## A dialog

```kotlin
OverlayDialog(
    isVisible = isAsking,
    onDismissRequest = { isAsking = false },     // scrim tap and back; must hide it
    alignment = Alignment.Center,
    isDismissibleOutside = true,
    isDismissibleByBack = true,
) {
    Text("Delete this post?")
    Row { TextButton(onClick = { isAsking = false }) { Text("Cancel") } }
}
```

Several dialogs may be visible: the last shown is on top, the others come back as it leaves. Focus moves
into the dialog if the screen had focus (a text field's keyboard closes) and returns after.

## A bottom sheet

```kotlin
OverlayBottomSheet(
    isVisible = isPicking,
    onDismissRequest = { isPicking = false },
    isDraggable = true,
    dragHandle = { SheetDragHandle() },           // null for none
) {
    LazyColumn(Modifier.heightIn(max = 480.dp)) { items(options) { OptionRow(it) } }
}
```

As tall as its content. Dragging past `BottomSheetStyle.dismissThreshold` or flinging down closes it; a
list inside scrolls first and, at its top, hands the drag to the sheet. The content behind is pushed back
as it rises (`OverlayBackdropStyle`).

### Navigation 3

A sheet as a navigation destination is a scene whose content is a sheet that is always visible:

```kotlin
class SheetScene<T : Any>(/* key, entries… */ private val onBack: () -> Unit) : OverlayScene<T> {
    override val content: @Composable () -> Unit = {
        OverlayBottomSheet(isVisible = true, onDismissRequest = onBack) { entries.last().Content() }
    }
}
```

The sheet leaves with its scene: popping the destination removes it at once, without the slide out.

## A dropdown menu

```kotlin
OverlayDropdown(
    isExpanded = isOpen,
    onExpandedChange = { isOpen = it },
    menuContent = { options.forEach { MenuRow(it, onClick = { pick(it); isOpen = false }) } },
    placement = OverlayPlacement.Bottom,
    isWidthMatchingAnchor = true,
) {
    FieldLooking(text = selected)                 // the anchor; a tap on it toggles the menu
}
```

Not modal: the screen keeps working while it is open, and a press outside closes it without being
swallowed. If the anchor already handles its own click (a `Button`), pass `isToggledByAnchor = false`
and toggle from that click, or the two toggles cancel out.

## A tooltip

```kotlin
OverlayTooltip(tooltipContent = { BasicText("Copied", style = TextStyle(color = Color.White)) }) {
    Icon(…)
}
```

A tap on the anchor toggles it; it hides after `TooltipStyle.autoDismissMillis` (0 keeps it); a tap
anywhere closes it. Drive it from outside with `isVisible` + `onVisibilityChange`. It flips to the other
side, or slides along the edge, when there is no room; the caret keeps pointing at the anchor.

## A snackbar

```kotlin
val snackbar = LocalSnackbarManager.current
snackbar.show(
    SnackbarMessage(
        text = "Saved",
        kind = SnackbarKind.Success,
        position = SnackbarPosition.Bottom,
        actionLabel = "Undo",
        onAction = { undo() },
    ),
)
```

Messages queue and show one at a time. Pressing one holds its timer; swiping it toward its edge closes
it. A whole custom row: `SnackbarStyle(content = SnackbarContent { message, onAction -> … })`.

From a ViewModel, route an effect to the manager at the screen (or the root), not the manager into the
ViewModel. With arch-kit's `UiString`, resolve it first: `SnackbarMessage(text = message.text.resolve())`.

## A showcase

```kotlin
OverlayShowcase(
    isVisible = !hasSeenBadgeTip,
    onDismissRequest = { markBadgeTipSeen() },    // persist, then hide
    tooltipContent = { BasicText("New: badges", style = TextStyle(color = Color.White)) },
    anchorShape = CircleShape,
) {
    Badge()
}
```

Dims everything but a cut-out around the anchor and points a balloon at it. Shown once the anchor is laid
out; closed by any tap or back.

## Previews

Nothing to install: without renderers — a `@Preview` — `OverlayDialog` and `OverlayBottomSheet` draw
their surface in place, the anchored overlays draw only their anchor, `OverlayHost` draws the content and
`LocalSnackbarManager` drops messages.

## Tests

- A ViewModel or screen that shows messages: provide `RecordingSnackbarManager` (`overlay/testing`) as
  `LocalSnackbarManager` and assert `messages`.
- A UI test of a screen with overlays: `CompositionLocalProvider(*InlineOverlayRenderers.values().toTypedArray())`
  draws every overlay in place while it is visible — no host, no animation — so the test finds its content.
- A renderer of your own (`DialogRenderer`…) in `Local…Renderer` replaces any overlay, in a test or an app.
- A custom modal state (`AnimatedModalOverlayState`) or anything animating with `Animatable`:
  `runTest(context = ImmediateFrameClock()) { … }` runs the animations to the end at once.
