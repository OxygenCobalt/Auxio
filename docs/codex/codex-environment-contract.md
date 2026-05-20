# Codex environment contract

Expected: Linux + bash + git + recursive submodules + JDK + Gradle wrapper + Android SDK + SDK platform/build tools + NDK + CMake + ninja.

No emulator or TS18 hardware is assumed.

Failure classes:
- `OK`
- `WARN_OPTIONAL_TOOL_MISSING`
- `SNAPSHOT_LIMITATION`
- `SUBMODULE_BLOCKER`
- `UPSTREAM_MEDIA_QUIRK`
- `ANDROID_SDK_BLOCKER`
- `ANDROID_NDK_BLOCKER`
- `JAVA_GRADLE_COMPAT_BLOCKER`
- `DEPENDENCY_CACHE_BLOCKER`
- `CODEX_AGENT_OFFLINE_LIMITATION`
- `REAL_BUILD_FAILURE`
- `HARDWARE_ONLY_VALIDATION`
- `REQUIRED_DOC`

Hard-fail classes: `SUBMODULE_BLOCKER`, `ANDROID_SDK_BLOCKER`, `ANDROID_NDK_BLOCKER`, `JAVA_GRADLE_COMPAT_BLOCKER`, `DEPENDENCY_CACHE_BLOCKER`, `REAL_BUILD_FAILURE`.
Warn-only classes: remaining classes.

Every script must end with a summary report.
