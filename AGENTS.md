# overlay-kit — rules for agents

overlay-kit is a **kit**: reusable Compose Multiplatform code that applications copy — renamed into their own
package — with `skillctl.sh kit install overlay-kit` (the skill-manager skill, `Thernal/knowledge`), and later
merge changes from with `kit update`. What they copy is listed in `kit.yml`: the modules `overlay/api`, `overlay/impl`, `overlay/wiring`, `overlay/testing`.
Everything committed to `main` reaches every app that takes the next update, so this file's first rule is
about delivery.

## Delivering a change

- **Nothing reaches `main` without a green `./gradlew build`** — every target, the tests on the JVM host
  and the iOS simulator, Detekt. Check Gradle's own exit code (`./gradlew build && git commit …`), never
  through a pipe: `./gradlew … | tail && git commit` checks `tail`. A failing test is fixed, never skipped.
- Git conventions, attribution (off here) and branches: `.agents/workspace.md`. A breaking change to what
  apps use is `feat!:`/`refactor!:` with a `Migration:` paragraph — apps merge the kit's files, never their
  own call sites.
- Detekt fails the build on any finding. `./gradlew build -PdetektAutoCorrect=true` fixes formatting;
  the rest is fixed by hand (named arguments, braces on every branch, `is`-prefixed booleans, block bodies).
  A `@Suppress` names the rule and says why, on the narrowest element.

## Writing code here

- **No `api(...)`** (epic D24): a module declares everything it uses; nothing is re-exported.
- **Rename-safe.** An app's copy is renamed textually (`kit.yml`: package, module, alias). Write the
  package prefix whole, never split or as a regex fragment; `libs.plugins.<alias>.` literally; and keep the
  kit's name out of any string an install does not rename (resource names, authorities, cache paths).
- **No Material.** The kit's modules use Compose runtime, foundation, ui and animation, and
  navigationevent for back; only the sample may use Material. An overlay draws its text with
  `BasicText` and takes everything else as slots or style values.
- **Contracts in `api`, work in `impl`** (epic D69, paging-kit's shape). A public overlay composable in
  `api` is a thin call to its `…Renderer` from a CompositionLocal whose default previews it; `impl`
  implements the renderer, `wiring` contributes it to the app graph. Nothing in `api` imports `impl`.
- **In-tree, never a window.** Overlays are composed by `OverlayPluginLayers` in the app's own tree — no
  `Popup`, no `Dialog` — so a new overlay is an `OverlayLayerPlugin`. A modal one marks itself with
  `OverlayModalEffect`, moves focus with the `ModalFocusEffect` pattern, and adds `OverlayBackHandler`
  only while it is on screen (the dispatcher gives back to the handler added last).
- **Layout-phase values are read in draw.** What `AnchoredOverlay` resolves during layout (placement,
  caret offset) is read in layer and draw blocks, never in composition; keep it that way, or overlays
  lag a frame behind their anchors.
- **The kit carries no copy.** Accessibility labels live in `OverlayStrings` with English defaults the
  app replaces; no other string reaches the user.
- `api`/`impl` code lives in `domain` or `presentation` packages, and layers point inwards (the kit's
  own Detekt rules); below them, packages are named by feature (`host`, `placement`, `dialog`…).
  `wiring` holds the Metro binding container only.
- Kotlin nests block comments: never write `/*` inside KDoc (`image/*`, `ios/*.swift`).
- Every module that changes what apps copy updates `kit.yml` in the same change: `code`, `surface`,
  `requires`.

## Documentation

| File | Holds | Update when |
|---|---|---|
| `README.md` | what the kit does, its layout, how it is built | anything a user of the kit sees changes |
| `overlay/README.md` | why each part has its shape | a decision or trade-off changes |
| `overlay/api/README.md` | how to use the overlays, task by task | the contract changes |
| `overlay/impl/README.md` | how to build an overlay of the app's own on the host | the host's building blocks change |
| `docs/todos/` | open questions, one file each | a question opens or is decided (then delete it) |
| `skills/overlay-kit` | the same for an agent in an app that took the kit | the public surface changes — `kit status` flags a skill older than the surface (LAG) |
| `kit.yml` | what an app copies and what its build must provide | a module, file part or requirement changes |

The skill's frontmatter has to load in Claude Code and in Codex alike (knowledge `docs/SKILLS.md`): a
`description` of at most 1024 characters — 600–900 in practice, since an app's renamed package can
lengthen it and every installed skill shares one context budget — valid YAML (no `": "` or `" #"` in a
plain one-line description; write `—`), and only the keys `name` and `description`. skill-manager warns
in an app whose copy breaks this; the fix is made here.

Docs are read in place by apps (`knowledgectl.sh kit overlay-kit read <path>`), never copied. When `kit.yml`
or `README.md` changes, the knowledge card `kits/overlay-kit.md` in `Thernal/knowledge` needs its `card_sha`
bumped (`scripts/check-corpus.py --kits` says so).

## The sample

`sample/` shows every overlay on Android and iOS and is never copied into apps. A sample that builds is
not one that starts: after changing it, build the iOS app with `xcodebuild` and launch it on a simulator
(`xcrun simctl launch --console-pty …`) — Compose Multiplatform refuses to start without
`CADisableMinimumFrameDurationOnPhone` in Info.plist, and a crash there shows nowhere else.
