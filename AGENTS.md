# AGENTS.md — Auxio-TS

## Repository purpose

Auxio-TS is a TS18/Topway/DoFun Variety-compatible variant of Auxio. Its primary purpose is to replace the stock `twmusic` / `com.tw.music` music app and integrate with the DoFun Theme music widget/panel on Topway head units.

## Priority order

1. **DoFun Variety compatibility** (`com.dofun.variety`) is the highest priority. The launcher/theme must recognise the app, open it from the music panel/widget, display playback state, and route music controls.
2. **Stock `twmusic` replacement contract** — the app installs as `com.tw.music` with component `com.tw.music.MusicActivity`.
3. **Auxio upstream** is the runtime base, not the product focus. Keep upstream source required to compile; do not add generic Auxio docs.
4. **UI development and visual validation** are in scope when they support active Auxio-TS app/UI work (Roborazzi/Robolectric preferred).
5. **CI/release/developer reliability** should stay concise and operational.
6. **Historical research** is out of scope. Do not add investigation archives, evidence packs, Codex prompts, or broad TS18 research.

## Critical constraints

- The `topwayTwMusicRelease` variant **must** install as exact package `com.tw.music`.
- The component `com.tw.music.MusicActivity` **must** remain available as an activity-alias.
- Standard Auxio identity (`org.oxycblt.auxio`) **must not** be accidentally broken.
- **Do not modify DoFun Theme** or any third-party APK.
- **Do not fake** `cn.cardoor.libs.media.RemoteMediaService` without a proven binder protocol.
- **Do not overclaim** TS18/DoFun runtime compatibility from static checks, emulator runs, or Roborazzi. Full launcher/widget parity requires real head-unit validation.
- **Do not add** broad research docs, evidence packs, prompt archives, or speculative TS18 research.
- **Do not add** screenshot/evidence-pack workflows that serve only old TS18 research archives.
- **UI regression workflows** (Roborazzi, layout screenshots) are allowed when they support active Auxio-TS app/UI development.

## Forbidden in product code

- `android.tw.john` / private TW hooks
- `com.tw.service.xt` / vendor binders
- `ITWCommandAidl` / private AIDL
- `android:sharedUserId` / system UID
- Copied smali / decompiled vendor code
- Reflection into vendor frameworks

## Allowed only in isolated Topway bridge

The following strings are allowed **only** in `app/src/main/java/org/oxycblt/auxio/headunit/topway/` and corresponding test paths:

- `com.tw.music.action.cmd` / `.prev` / `.next` / `.pp`
- `com.tw.music.info`
- `com.tw.launcher.music_progress_duration`
- `com.android.launcher.widget_music_progress`

## Package identity isolation

`com.tw.music` as applicationId is allowed **only** in:

- `app/build.gradle` (topwayTwMusic flavour)
- `app/src/topwayTwMusic/AndroidManifest.xml`
- `app/src/topwayTwMusic/res/values/donottranslate.xml`
- `app/src/topwayTwMusicDebug/res/values/donottranslate.xml`

## Build and validation

```sh
bash scripts/prepare-ci-environment.sh
./gradlew :app:assembleStandardDebug
./gradlew :app:assembleTopwayTwMusicRelease
bash scripts/check-dofun-topway-compat.sh
bash scripts/check-headunit-compat-safety.sh
```

Always run both compatibility scripts before finishing a PR.

## CI workflows

| Workflow | Purpose |
|----------|---------|
| `android.yml` | Build standard + Topway debug APKs, DoFun compatibility checks |
| `lint.yml` | Formatting, unit tests, lint, head-unit safety checks |
| `manual-release.yml` | Signed release with both APK variants |
| `ui-screenshots.yml` | Manual Roborazzi UI regression screenshots (no emulator required) |

UI development is in scope. Do not delete `ui-screenshots.yml` or Roborazzi test files
merely because they mention screenshots. Roborazzi runs via Robolectric — no emulator
or device needed. Artifacts (PNG outputs, HTML reports) are uploaded to GitHub Actions.

## Documentation

Keep docs minimal and operational:

- `docs/DOFUN_VARIETY_COMPATIBILITY.md` — integration reference
- `docs/TS18_RUNTIME_VALIDATION.md` — head-unit validation checklist
- `docs/RELEASE_WORKFLOW.md` — release process
- `docs/DEVELOPMENT.md` — setup and layout

## Agent rules

1. Do not add new documentation files without explicit approval.
2. Do not create archive directories or move old content instead of deleting it.
3. Do not add speculative features or probe frameworks.
4. Every PR must pass `check-dofun-topway-compat.sh` and `check-headunit-compat-safety.sh`.
5. Large tasks must deliver runtime code, not just docs/tests/templates.
6. Treat keystore/signing edits as security-sensitive.
7. Preserve MediaSession/MediaBrowser behaviour and Topway broadcast compatibility.
8. The Topway bridge must remain isolated — no vendor strings outside the bridge path.
9. Run Gradle builds when the environment supports it.
10. Keep the standard variant functional as a development baseline.
11. Report clearly when a result is static/build-only versus TS18 hardware-validated; never present unvalidated DoFun widget behavior as proven.
12. Do not delete `ui-screenshots.yml` or Roborazzi test files — UI regression coverage is in scope for Auxio-TS active development.
13. Do not delete evidence-pack or old TS18 research workflows if they are already absent; do not re-add them.
