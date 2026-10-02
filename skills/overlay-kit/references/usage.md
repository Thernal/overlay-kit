# Usage

## Dialog

```kotlin
var isAsking by remember { mutableStateOf(false) }
OverlayDialog(isVisible = isAsking, onDismissRequest = { isAsking = false }) {
    Text("Delete this post?")
    TextButton(onClick = { isAsking = false; onDelete() }) { Text("Delete") }
}
```

`alignment`, `isDismissibleOutside`, `isDismissibleByBack`, `style`. Several visible: last shown on top.

## Bottom sheet

```kotlin
OverlayBottomSheet(isVisible = isPicking, onDismissRequest = { isPicking = false }) {
    LazyColumn(Modifier.heightIn(max = 480.dp)) { items(options) { Option(it) } }
}
```

As tall as its content — bound a list's height. A list inside hands the drag to the sheet at its top.
`isDraggable = false` pins it; `dragHandle = null` drops the handle.

Navigation 3 destination: an `OverlayScene` whose content is
`OverlayBottomSheet(isVisible = true, onDismissRequest = onBack) { entries.last().Content() }`. It leaves
with the scene, without the slide out.

## Dropdown

```kotlin
OverlayDropdown(
    isExpanded = isOpen,
    onExpandedChange = { isOpen = it },
    menuContent = { items.forEach { Row(Modifier.clickable { choose(it); isOpen = false }) { Text(it) } } },
) { SelectedField(value) }
```

`placement`, `isWidthMatchingAnchor`, `isDismissibleOutside`. Clickable anchor content →
`isToggledByAnchor = false`.

## Tooltip

```kotlin
OverlayTooltip(tooltipContent = { BasicText("Copied", style = TextStyle(color = Color.White)) }) { CopyIcon() }
```

Controlled: `isVisible` + `onVisibilityChange` (both). `TooltipStyle(autoDismissMillis = 0)` keeps it.

## Snackbar

```kotlin
LocalSnackbarManager.current.show(
    SnackbarMessage(text = "Saved", kind = SnackbarKind.Success, position = SnackbarPosition.Bottom,
        actionLabel = "Undo", onAction = ::undo),
)
```

Queued one at a time; `durationMillis` per message; `dismiss()` closes the current one. From a
ViewModel, emit an effect and show it where the manager is — resolve `UiString`s to text first.

## Showcase

```kotlin
OverlayShowcase(
    isVisible = !seen,
    onDismissRequest = { markSeen() },
    tooltipContent = { BasicText("New here", style = TextStyle(color = Color.White)) },
    anchorShape = CircleShape,
) { Badge() }
```

## Previews and tests

- Previews need nothing: without renderers, dialogs and sheets draw their surface in place, anchored
  overlays draw their anchor, `OverlayHost` draws the content.
- UI tests of a screen: `CompositionLocalProvider(*InlineOverlayRenderers.values().toTypedArray())` draws
  every overlay in place while visible — no host, no animation. A renderer of your own (`DialogRenderer`…)
  in its `Local…Renderer` replaces one overlay.
- Messages: `CompositionLocalProvider(LocalSnackbarManager provides RecordingSnackbarManager())` and
  assert `messages`.
- Animations in a test: `runTest(context = ImmediateFrameClock()) { … }`.
