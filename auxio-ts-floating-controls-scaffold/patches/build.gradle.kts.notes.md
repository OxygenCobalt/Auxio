# Gradle integration notes

This scaffold avoids new dependencies by using programmatic Android views. Codex should still inspect the real Auxio-TS build before changing Gradle.

Possible needs:

- AndroidX Core for `ContextCompat.startForegroundService`, if not already present.
- Lifecycle dependency only if using `ProcessLifecycleOwner`; otherwise use ActivityLifecycleCallbacks or existing app lifecycle hooks.
- Compose not required for MVP.

Recommended: keep the first implementation dependency-light and variant-scoped to `topwayTwMusic`.
