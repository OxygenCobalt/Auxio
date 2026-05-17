# Source-Shim Runtime Import Pipeline

Last updated: 2026-05-08 (UTC)

> **Execution authority:** `docs/migration-blueprint.md` is the only live execution tracker and gate source. This file is implementation-reference only and non-authoritative for live phase/gate status.

Current canonical gate/readiness state and next action:
- `docs/migration-blueprint.md` (workstream: Source-shim runtime import, Gate S)

## Why source-shim exists

The media bridge is authored in maintainable Java under `src-shim/java/com/tw/music/media/` so MediaSession/metadata/state behavior can be iterated safely before committing runtime smali. Runtime behavior still requires final classes to exist under `app/apktool/smali*`.

## Required inputs

- Preinstalled `javac` (no install/download from repo scripts).
- Preinstalled `jar` (used to verify required legacy support classes before compile).
- `ANDROID_JAR` (API 29-compatible `android.jar`).
- `SUPPORT_CLASSPATH` containing one or more jars/classes.jar entries that together provide:
  - `android.support.v4.media.*`
  - `android.support.v4.media.session.*`
  - `android.support.v4.media.app.*`
  - `android.support.v4.app.*`

## Optional tools (only if already preinstalled)

- `d8` or `dx` to convert compiled classes into dex.
- `baksmali` (binary or jar) to disassemble dex into importable smali.

The pipeline never installs tools and never downloads dependencies.

## Providing classpath inputs

Preferred explicit invocation:

```bash
ANDROID_JAR=/abs/path/android-29/android.jar \
SUPPORT_CLASSPATH=/abs/path/support-compat-classes.jar:/abs/path/support-media-compat-classes.jar \
bash scripts/build_source_shim.sh
```

The script also attempts auto-detection by searching `$ROOT_DIR`, `$ANDROID_HOME`, `$ANDROID_SDK_ROOT`, `$HOME/Android/Sdk`, and `$HOME/Library/Android/sdk` for suitable jars.

Optional AAR input (local-only extraction of `classes.jar` into `.local/source-shim-build/classpath/`):

```bash
ANDROID_JAR=/abs/path/android-29/android.jar \
SUPPORT_AARS=/abs/path/support-compat-28.0.0.aar:/abs/path/support-media-compat-28.0.0.aar \
bash scripts/build_source_shim.sh
```

Backward compatibility: `SUPPORT_V4_JAR` is still accepted as a single-entry fallback and mapped into `SUPPORT_CLASSPATH`.

## Script behavior

`bash scripts/build_source_shim.sh`:
- must be run from repository root;
- cleans `$OUT_DIR` (`.local/source-shim-build`) to avoid stale artifacts;
- compiles Java shim sources into `.local/source-shim-build/classes`;
- if `d8`/`dx` exists, emits `.local/source-shim-build/dex/classes.dex`;
- if baksmali exists, emits smali into `.local/source-shim-build/smali`;
- fails fast with exact missing-input messages when required inputs are absent;
- fails fast before compile when required support classes are absent:
  - `android/support/v4/media/MediaMetadataCompat.class`
  - `android/support/v4/media/session/MediaSessionCompat.class`
  - `android/support/v4/media/session/PlaybackStateCompat.class`
  - `android/support/v4/app/NotificationCompat.class`
  - `android/support/v4/media/app/NotificationCompat$MediaStyle.class`

## Output location and commit policy

All generated outputs are local-only under `.local/source-shim-build/`. Generated binary/runtime artifacts (`.class`, `.dex`, tool-generated smali) are not committed directly without manual review.

## Static workflow requirements

After source-shim imports or smali/runtime touch points, run:

```bash
bash -n scripts/codex/setup_readability_env.sh
bash -n scripts/codex/maintain_readability_env.sh
bash -n scripts/build_source_shim.sh
python3 tools/readability/07_validate_readability_reports.py
python3 tools/smali/validate_smali_static.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
git diff --stat
git diff --numstat
git status --short
```

Optional local build check:

```bash
apktool b app/apktool -o .local/<task-name>-check.apk
```

Do not commit `.local` APK outputs.

## Smali review before import

Before copying into `app/apktool/smali*`:
1. Verify classes are limited to `com/tw/music/media/*`.
2. Confirm no unexpected package renames, manifest-affecting symbols, or vendor-token edits.
3. Confirm support-library references resolve against app runtime.
4. Keep import scope minimal and reviewable per file.

## Gate before MusicService wiring

Do not wire `MusicService` bridge hooks until all are true:
- runtime bridge classes are committed under `app/apktool/smali*/com/tw/music/media/`;
- static grep finds `Lcom/tw/music/media/MediaControlBridge;` in canonical smali;
- no unresolved support-library class references in imported bridge smali;
- identity surfaces are unchanged (`com.tw.music`, `android.uid.system`, version fields);
- TW command/widget/vendor boundaries remain unchanged.

## Runtime import destination + static proof

- Runtime destination for reviewed bridge classes: `app/apktool/smali_classes3/com/tw/music/media/`.
- Alternate `smali_classes*` destinations are allowed only when method/field reference pressure requires it and the PR explicitly justifies placement.
- Static proof command for Gate S:

```bash
grep -Rn "Lcom/tw/music/media/MediaControlBridge;" app/apktool/smali*
```

If grep has no hits, Gate S remains blocked and `MusicService` must remain untouched.
