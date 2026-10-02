# overlay-kit

Overlays for a Compose Multiplatform app (android, iosArm64, iosSimulatorArm64) built on foundation
alone — no Material: dialogs, bottom sheets, dropdown menus, tooltips, snackbars and showcases, all drawn
by one host at the root of the app, in the app's own composition rather than in separate windows.

```kotlin
@Composable
fun AppRoot() {
    OverlayTheme(styles = appOverlayStyles()) {       // the app's tokens, mapped once
        OverlayHost {                                 // every overlay of the kit, stacked right
            AppContent()
        }
    }
}

@Composable
fun DeleteButton(onDelete: () -> Unit) {
    var isAsking by remember { mutableStateOf(false) }
    Button(onClick = { isAsking = true }) { Text("Delete") }
    OverlayDialog(isVisible = isAsking, onDismissRequest = { isAsking = false }) {
        Text("Delete this post?")
        TextButton(onClick = { isAsking = false; onDelete() }) { Text("Delete") }
    }
}
```

Because the overlays are in the app's composition, the content behind a sheet can be pushed back as it
rises, one stack orders every overlay, and Android and iOS behave alike. What a separate window gives for
free the host does itself: the content behind a modal is hidden from screen readers and from keyboard
focus, focus moves into the modal and comes back after, and back — the button, Android's predictive
gesture, iOS's edge swipe — closes the top overlay.

## Documentation

| Read | For |
|---|---|
| this file | what is here and how it is built |
| [`overlay/components/README.md`](overlay/components/README.md) | using the overlays, task by task: the root, each overlay, styling, previews, tests |
| [`overlay/core/README.md`](overlay/core/README.md) | the host underneath: writing an overlay plugin of your own, anchored placement, the modal lifecycle |
| [`overlay/README.md`](overlay/README.md) | why each part has its shape, and what changed from the app it came from |
| [`skills/overlay-kit`](skills/overlay-kit/SKILL.md) | the same for an agent working in an app that uses the kit |

## Installing

An application takes the kit **by copy**, not as a dependency: the code is copied into the app, renamed to the app's own package, and belongs to the app from then on. Nothing is published to a Maven repository.

### With skill-manager

If you have access to the author's knowledge repository (`github.com/Thernal/knowledge`), its **skill-manager** skill does all of it — copy, rename, the skill, and later updates:

```sh
skillctl.sh kit install overlay-kit --package com.example.app --module :core:overlay \
    --alias app
```

It copies the `code` parts of [`kit.yml`](kit.yml) renamed, installs the `overlay-kit` skill and records the copy in `kits.lock`. `kit status` then shows what changed upstream and what the app edited; `kit update` merges the kit's changes three ways, keeping the app's edits. The install prints what the app must provide (`requires`).

### Without it

The same by hand, from a clone of this repository.

1. **Copy** the paths listed under `code` in [`kit.yml`](kit.yml) into the app, under the module path the app gives them: `overlay/…` → `core/overlay/…`. Note the commit you copied (`git rev-parse HEAD`) — updates start from it.
2. **Rename** in everything copied:

   | In the kit | Becomes | Where |
   |---|---|---|
   | `io.thernal.overlaykit` | the app's package, e.g. `com.example.app` | sources, build files; and the directories `io/thernal/overlaykit` |
   | `:overlay:` and `":overlay"`, `projects.overlay.` | the module path, e.g. `:core:overlay:`, `projects.core.overlay.` | build files |
   | `libs.plugins.overlaykit.` | the app's catalog alias, e.g. `libs.plugins.app.` | build files |

   ```sh
   # in the app, after copying — perl, so it runs the same on macOS and Linux
   grep -rlI -e io.thernal.overlaykit -e io/thernal/overlaykit -e :overlay -e plugins.overlaykit. core/overlay \
     | xargs perl -pi -e 's/\Qio.thernal.overlaykit\E/com.example.app/g; s{\Qio/thernal/overlaykit\E}{com/example/app}g; s/\Q:overlay:\E/:core:overlay:/g; s/"\Q:overlay\E"/":core:overlay"/g; s/projects\.\Qoverlay\E\./projects.core.overlay./g; s/libs\.plugins\.\Qoverlaykit\E\./libs.plugins.app./g'
   find core/overlay -depth -type d -path '*/io/thernal/overlaykit' | while read -r d; do
     mkdir -p "${d%/io/thernal/overlaykit}/com/example" && mv "$d" "${d%/io/thernal/overlaykit}/com/example/app"
   done
   find core/overlay -depth -type d -empty -delete
   ```

3. **Provide** what the copy expects — the `requires` list in [`kit.yml`](kit.yml): convention plugins (build-kit's, or the ones in this repository's `build-logic/convention`), catalog entries, settings.
4. **The skill** (optional): copy [`skills/overlay-kit`](skills/overlay-kit) into the app's skills directory (`.claude/skills/` for Claude Code), with the same renames, so an agent working in the app knows the kit.
5. **Updates** are yours to carry: `git diff <the commit you copied> <a newer one> -- <the code paths>` in the kit shows what changed; apply what you want, renamed the same way.

## Layout

| Module | Holds | Depends on |
|---|---|---|
| `overlay/core` | the plugin host (`OverlayPluginHost`, `OverlayLayerPlugin`, `OverlayLayers`), the backdrop, anchors, anchored placement (`AnchoredOverlay`), the modal lifecycle (`ModalOverlayState`), the scrim, back handling | Compose, navigationevent |
| `overlay/components` | `OverlayHost` (every overlay installed), `OverlayDialog`, `OverlayBottomSheet`, `OverlayDropdown`, `OverlayTooltip`, `OverlayShowcase`, the snackbar (`SnackbarManager`), their plugins and styles, `OverlayTheme` | core, Compose |
| `overlay/testing` | `RecordingSnackbarManager`, `ImmediateFrameClock` | components |
| `sample/shared`, `sample/android`, `sample/ios` | one screen with every overlay, and a switch to an app's mapped styles | the kit — never copied |

## Building

```sh
./gradlew build
```

Every target, tests on the JVM host and the iOS simulator — placement geometry and the resolver, the modal
lifecycle, the sheet's settle rule, the snackbar queue (timers, holding, stale timers), the testing doubles
— Detekt, which fails on any finding (`-PdetektAutoCorrect=true` fixes formatting first), Android lint on
the sample, and the sample's iOS framework link. Building for iOS needs Xcode, not only its command-line
tools.

The iOS sample is an Xcode project (`sample/ios/Sample.xcodeproj`, generated from `project.yml` by
XcodeGen); its build phase compiles the framework with Gradle:

```sh
xcodebuild -project sample/ios/Sample.xcodeproj -scheme Sample -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' build
```
