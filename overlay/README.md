# overlay — design

Why each part has the shape it has, and what changed from Act2Act's `uikit/components`
(`core/overlay`, `product/{dialog,bottomsheet,dropdown,tooltip,snackbar,showcase}`), where it came from.
The epic's decisions are D62–D67.

## One host in the app's own tree

Every overlay is composed by `OverlayLayers`, above the app's content, in the same composition. None
opens a `Popup` or `Dialog` window — Material's sheets and dialogs do, on Android. Staying in the tree is
what makes three things possible:

- **the backdrop** — the content behind a rising sheet is scaled, lifted and rounded
  (`OverlayBackdropStyle`); a window cannot transform what is under it;
- **one stack** — dialogs, sheets and the rest are layers of one box, ordered by plugin and, within a
  plugin, by the order they were shown;
- **one behaviour** — Android and iOS draw the same tree; there is no platform window to differ.

The price is what a window does for free, which the host now does itself (D67):

| A window would… | Here |
|---|---|
| hide what is behind it from screen readers | `OverlayLayers` clears the content's semantics while `OverlayModalController.isModalOpen` |
| keep keyboard focus inside | the content's focus group cancels focus entering it while a modal is open |
| take focus, closing a text field's keyboard | `takeBackgroundFocus()` saves the focused child and the modal requests focus — only if something had focus |
| give focus back | `restoreFocusedChild()` when the last modal closes |
| handle back | `OverlayBackHandler` on navigationevent, added only while the overlay is up so it outranks screens composed earlier |

## Plugins

`OverlayLayerPlugin` has two halves because they live at opposite ends of the composition: `Provide`
wraps the app's content (a controller in a CompositionLocal, so producers anywhere can register), and
`Render` draws above it. The split between `ProvideOverlayControllers` and `OverlayLayers` follows: the
controllers must be installed before any app-level provider that reads them, and the layers must come
after anything overlay content needs, since overlay content is composed at the layers' position.

A producer (`OverlayDialog(…)`) never draws. It republishes its entry — a value with fresh lambdas — after
every composition, and shows or hides its key; the host draws the top key's entry. Hosts key their
lifecycle on the top **key**, never the entry, or every producer recomposition would restart it
(`syncTopEntry` carries content updates). This is Act2Act's design, unchanged.

## What changed from Act2Act

| Act2Act | Here | Why |
|---|---|---|
| `DsTheme` tokens inside every overlay | `…Style` values with neutral defaults, `OverlayTheme(OverlayStyles)` once at the root | the kit cannot know an app's design system (D63) |
| `UiString`, MaterialSymbols icons in `SnackbarData` | `SnackbarMessage(text: String)`, icons per kind from the style, none by default | no dependency on arch-kit or an icon set (D3, D64) |
| `ComposableProvider`, `NoLocalProvider`, `ImmutableList` | `OverlayHost` / `ProvideOverlays` / `OverlayLayers` composables, `error(…)`, `List` | no dependency on the app's presentation layer or on kotlinx-collections |
| each app listing the plugins in its root | `OverlayHost` installs all six in a fixed order; an app adds its own with `extraPlugins` | the order is the kit's knowledge (a dialog above a sheet, the snackbar above all), and an unused plugin costs an idle controller (D68) |
| `BaseModalOverlayState`: `animate()` behind a `Mutex` | `ModalOverlayState` (lifecycle) + `AnimatedModalOverlayState` (`Animatable`) | `Animatable` serialises its own animations and keeps velocity when a show interrupts a hide |
| sheet drag: `draggable` + its own velocity projection | `AnchoredDraggableState` (hidden / expanded anchors), foundation's fling, `SheetNestedScrollConnection` | a list inside a sheet now hands over to the sheet at its top, as Material's do — Act2Act had no nested scroll |
| dropdown and tooltip placed with `onSizeChanged` + `offset`, the first frame drawn invisible, tooltips waiting two frames | `AnchoredOverlay`: one custom `Layout` measures, resolves and places in the same pass; what it resolved is read in draw | no invisible frame, no size written to state to be read back a frame later |
| anchors through `onGloballyPositioned` + `boundsInWindow` | `Modifier.overlayAnchor` through `onLayoutRectChanged` (no throttle, no debounce), in root coordinates, translated to the host's | cheaper, fires only when the rectangle moved; correct when the host does not start at the window's corner |
| `PlatformBackHandler` (expect/actual) | `OverlayBackHandler` on navigationevent, returning the gesture's progress | predictive back: dialogs shrink and sheets sink with the finger |
| no accessibility work | panes, traversal groups, a labelled scrim, hidden background, focus in and back, polite live regions for snackbars and tooltips | in-tree overlays get none of it from a window |
| snackbar controller with its own `CoroutineScope(Dispatchers.Main)` | `SnackbarController.run()`, launched by the plugin's composition | the timers die with the host; nothing to cancel by hand |
| a timer's `DismissCurrent` could close the next message | a timer's dismiss names its message and is dropped if another is up | a message closed by hand just as its timer fired no longer takes the next one with it |
| dropdown: touches outside fell through, the menu stayed open | a press outside closes it — observed on the content's initial pass, never consumed (`OverlayBackgroundTouches`) | the press still reaches what it landed on; a press on the anchor is left to the anchor |
| dropdown used the resolver directly; tooltip the calculator | both use `OverlayPositionCalculator` (keep the preferred side when it fits on its axis) | one placement rule |

Not taken: foundation's `BasicTooltipBox` — a `Popup` window, and experimental. The Navigation 3
`BottomSheetScene` / `ModalScene` stay in the app: an adapter of a few lines puts an `OverlayBottomSheet`
in an `OverlayScene` (`overlay/components/README.md` → Navigation 3).

## Placement

`OverlayPlacementGeometry` is the single source of anchored geometry; the resolver tries the preferred
placement and then a fixed order of alternatives, and the calculator pins the preferred placement while
it fits on its own axis, clamping only the cross axis — so a tooltip near an edge slides along the edge
instead of jumping to the anchor's other side. The caret offset is how far that clamp moved the popup, so
a caret drawn at centre plus offset still points at the anchor. Placements are `Start`/`End`, mirrored
under right-to-left once, before resolving.

The caret adds to the popup's size along the axis it points on, so the size depends on the placement:
`AnchoredOverlay` resolves with the preferred placement's size and, when the resolver moves the popup to
the other axis, once more with that axis's size.

## Tests

Pure logic is tested where it lives: geometry, resolver and calculator; the modal lifecycle with an
immediate frame clock; the sheet's settle rule; the snackbar queue on virtual time with the test
scheduler's time source. Composition-level behaviour (focus, semantics, gestures) is checked in the
sample on devices — `docs/todos/`.
