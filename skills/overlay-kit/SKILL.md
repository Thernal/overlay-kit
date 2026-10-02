---
name: overlay-kit
description: Writes, reviews and debugs overlay code in Compose Multiplatform apps that use overlay-kit (packages io.thernal.overlaykit.overlay.*; OverlayHost, OverlayLayerPlugin, OverlayDialog, OverlayBottomSheet, OverlayDropdown, OverlayTooltip, OverlayShowcase, LocalSnackbarManager, SnackbarMessage, OverlayTheme, OverlayStyles, AnchoredOverlay, ModalOverlayState, RecordingSnackbarManager). Use it for any dialog, bottom sheet, menu, tooltip, snackbar, toast, coach mark or popup work in such a project, even when the kit is not named — showing a confirmation, a picker sheet, an anchored menu, a message after an action, styling overlays with the app's tokens, a sheet as a Navigation 3 destination, an overlay that does not appear or appears without the app's theme, back not closing it, tests of messages. Not for projects without overlay-kit, and not for Material's own dialogs.
---

# overlay-kit

overlay-kit draws dialogs, sheets, menus, tooltips, snackbars and showcases from one host at the app's
root, in the app's own composition, on foundation only (no Material). Guide:
https://github.com/Thernal/overlay-kit — `overlay/components/README.md` (use), `overlay/core/README.md`
(build on it), `overlay/README.md` (why).

## 1. Orient first

```sh
grep -rn --include=*.kt -e "OverlayHost" -e "ProvideOverlays(" -e "OverlayLayers(" .   # the root, extraPlugins
grep -rn --include=*.kt -e "OverlayStyles(" -e "OverlayTheme(" .                              # token mapping
grep -rn --include=*.kt -e "OverlayDialog(" -e "OverlayBottomSheet(" -e "LocalSnackbarManager" . | head  # call sites to copy
```

No host → [references/setup.md](references/setup.md). **Taken as a kit?** A `kits.lock` naming
`overlay-kit` means the modules were copied renamed with skill-manager — this skill too.
`skillctl.sh kit status overlay-kit` shows what moved upstream; offer `kit update overlay-kit` rather
than hand edits.

## 2. The model

- A call site owns the state: `OverlayDialog(isVisible, onDismissRequest) { … }`. `onDismissRequest`
  (scrim, back, drag) must set `isVisible = false` — the overlay never hides itself.
- The call site draws nothing; the plugin at the host does. **Overlay content is composed at the host**:
  a CompositionLocal provided between the host and the call site is invisible inside the overlay.
- Looks come from `…Style` values, defaulting to `OverlayTheme.styles`; the app maps its tokens once in
  `OverlayStyles`. Text inside overlays is the app's own composables.
- Snackbar: `LocalSnackbarManager.current.show(SnackbarMessage(text, kind, position, actionLabel, onAction))`.
- Modal overlays hide the screen from accessibility and keyboard focus, take focus if the screen had it,
  and close on back (predictive back included). Dropdowns are not modal.

## 3. Rules

- Never wrap an overlay in `Popup`/`Dialog`, and never use Material's for the same job in this app —
  mixed stacks draw in the wrong order and lose the backdrop.
- `OverlayHost` installs all six; an app's own overlay joins with `extraPlugins`, never a second host.
- Providers overlay content needs (theme, strings) go **above** `OverlayHost` — or split it into
  `ProvideOverlays` … `OverlayLayers`.
- A dropdown whose anchor is itself clickable: `isToggledByAnchor = false`, toggle from the anchor.
- Strings screen readers hear are `OverlayStrings` — translate them in `OverlayStyles(strings = …)`.
- A new kind of overlay is an `OverlayLayerPlugin` (`overlay/core/README.md`), not a component that
  draws itself in place.

## 4. Tasks

| Task | Read |
|---|---|
| install the host, the plugins, the styles; Android back | [references/setup.md](references/setup.md) |
| a dialog, a sheet (lists inside, Navigation 3), a menu, a tooltip, a snackbar, a showcase | [references/usage.md](references/usage.md) |
| previews and tests | [references/usage.md](references/usage.md) → Previews and tests |
| an overlay of your own, anchored placement | `overlay/core/README.md` in the kit |

## 5. When it misbehaves

| Symptom | Cause |
|---|---|
| `No DialogPlugin: wrap the app in OverlayHost.` | no host above the call (a second compose root, a test) |
| the overlay ignores the app's theme or a local | that provider sits below the host — move it above |
| the dropdown opens and closes at once | the anchor's own click toggles too — `isToggledByAnchor = false` |
| back closes the screen, not the overlay | another back handler added later; or (Android) not a `ComponentActivity` |
| no predictive back animation on Android | `android:enableOnBackInvokedCallback="true"` missing |
| the sheet does not close when dragged | `onDismissRequest` does not set `isVisible = false` |
