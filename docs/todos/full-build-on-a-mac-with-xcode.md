# The full build has not run yet

The kit was written on a machine with Xcode's command-line tools only and a network that intercepts TLS
for `dl.google.com` (a corporate proxy) — Gradle's JDK trusts neither that proxy's CA nor Maven Central's
new Let's Encrypt chain for artifacts not yet in the cache. What ran there, green:

- every module compiled for Android and for iOS (klibs), the sample's shared code included;
- every test on the JVM host (34);
- Detekt on every module, the sample's shared code included.

What did not run:

- the tests on the iOS simulator and the iOS framework link (need Xcode);
- `:sample:android` — assembling and lint (needed artifacts behind the proxy);
- `xcodebuild` of `sample/ios` and a launch on the simulator.

**Done when** `./gradlew build` passes on a Mac with Xcode, the iOS sample builds and launches
(`xcrun simctl launch --console-pty …`), and only then the work branch is merged into `main` (AGENTS.md).
