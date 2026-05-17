# Pixel 9a / Android 16 compatibility build (development smoke path)

This path adds a **separate** Pixel test artifact and does not change the canonical TS18 system-app release surface.

- Canonical TS18 source tree remains `app/apktool/` with `android:sharedUserId="android.uid.system"` retained.
- Pixel path builds from a temp copy, removes `sharedUserId` in the temp manifest, and overlays shim classes under `android.tw.john.*`.
- Intended outcome: install + launch smoke on Pixel 9a, not full TS18 feature parity.

## Build

```bash
bash scripts/10_build_pixel9a_compat.sh
```

Outputs:
- `dist/com.tw.music-pixel9a-compat-no-uid-unsigned.apk`
- optional signed variant when `KEYSTORE`, `KS_ALIAS`, `KS_PASS` are provided.

## Runtime harness

```bash
bash scripts/11_pixel_validation_harness.sh pixel9a-run.log \
  dist/com.tw.music-signed-no-uid-vX.Y.Z.apk \
  dist/com.tw.music-pixel9a-compat-no-uid-vX.Y.Z.apk
```

## Guardrails

- Do not ship Pixel compat APK as TS18 release candidate.
- Do not remove or weaken TW vendor hooks in canonical TS18 tree.
- Do not change package name `com.tw.music`.
