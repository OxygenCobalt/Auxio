# Manual Validation Runbook (Human-Executed)

Last updated: 2026-05-08 (UTC)

For live gate status, blockers, and pass/fail decisions, use `docs/migration-blueprint.md`.

## 1) Local repo sync before manual validation

```bash
cd ~/repos/twm/twmusic
git fetch origin
git status --short
git log --oneline -n 20
```

- Record the exact commit SHA that will be tested.
- Confirm no unexpected local edits before test execution.

## 2) Local static validation (required)

Run all commands from repo root:

```bash
bash -n scripts/codex/setup_readability_env.sh
bash -n scripts/codex/maintain_readability_env.sh
bash -n scripts/build_source_shim.sh
python3 tools/readability/07_validate_readability_reports.py
python3 tools/smali/validate_smali_static.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
```

If any command fails, stop and report the exact failing command + output.

## 3) Optional Apktool build check

```bash
command -v apktool && apktool b app/apktool -o .local/manual-apktool-check.apk || true
```

- If `apktool` is available, run the check build and record success/failure.
- If `apktool` is missing, do **not** install tools during this runbook; record tool absence as evidence.
- Do not commit `.local/*.apk` outputs.

## 4) Gate V TS18 runtime validation

### ADB/device preflight

```bash
adb devices -l
adb shell getprop ro.build.fingerprint
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release
```

### Install/update proof

```bash
adb shell pm path com.tw.music || true
adb install -r dist/com.tw.music-unsigned.apk
adb shell dumpsys package com.tw.music | sed -n '1,200p'
```

Capture install/update result and any signature/shared UID error text.

### Media/session evidence capture

1. Start baseline capture:
   - `adb logcat -c`
   - `adb logcat > gate-v-logcat.txt`
2. Capture session/notification baseline before launch:
   - `adb shell dumpsys media_session > gate-v-media_session-before.txt`
   - `adb shell dumpsys notification > gate-v-notification-before.txt`
3. Launch app and run actions: play, pause, next, previous, progress/seek.
4. Capture after-actions evidence:
   - `adb shell dumpsys media_session > gate-v-media_session-after.txt`
   - `adb shell dumpsys notification > gate-v-notification-after.txt`

### TW broadcast command parity

```bash
adb shell am broadcast -a com.tw.music.action.pp
adb shell am broadcast -a com.tw.music.action.next
adb shell am broadcast -a com.tw.music.action.prev
```

### Media dispatch parity

```bash
adb shell media dispatch play
adb shell media dispatch pause
adb shell media dispatch play-pause
```

### Widget/hardware/manual checks

- Verify widget play/pause/next/prev behavior.
- Verify steering-wheel/hardware key parity where available.
- Verify Maps/Waze/TLink/Android Auto/CarPlay coexistence where available on device.

### Evidence bundle packaging

Package artifacts with deterministic naming, for example:

- `gate-v-logcat.txt`
- `gate-v-media_session-before.txt`
- `gate-v-media_session-after.txt`
- `gate-v-notification-before.txt`
- `gate-v-notification-after.txt`
- `gate-v-widget-hardware-notes.md`
- `gate-v-environment.txt` (device + build fingerprint + tested commit SHA + APK SHA256)

## 5) GitHub PR/manual review steps

1. Open the PR in GitHub.
2. Read all unresolved conversations.
3. Check **Files changed** for protected surfaces (`reference/`, `app/apktool/smali*`, `app/apktool/res/`, manifest/version surfaces when out of scope).
4. Confirm CI/check results for required static gates.
5. Post reviewer replies where requested and link evidence files.
6. Merge only after static gates pass and critical review comments are resolved.

## 6) Evidence return format (paste back to Codex/ChatGPT)

Return a single report containing:

- Gate summary markdown (Gate R/S/M/V/RC pass/fail/blocked with reasons)
- `error-scan.txt` (if any failures)
- `media_session` snippets (before/after)
- `notification` snippets (before/after)
- manual widget/hardware notes
- exact APK SHA256
- exact commit SHA tested
