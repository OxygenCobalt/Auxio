# Manual gate: Android SDK Build Tools / aapt2

Apktool on Termux/Android must use an external aapt2 from official Android SDK
Build Tools — it cannot build with the internal bundled one reliably.

## Why the script looks for aapt2

The bootstrap already searches:
- `$AAPT2` environment variable
- `$PREFIX/libexec/aapt2` (Termux native binary, preferred over wrapper)
- `aapt2` in PATH
- `$ANDROID_SDK_ROOT/build-tools/**`
- `$ANDROID_HOME/build-tools/**`
- `.tools/android-sdk/build-tools/**`

## How to supply aapt2

**Option A — use an existing Android SDK on this device:**
```bash
export ANDROID_SDK_ROOT=/path/to/android-sdk
bash scripts/02_build_unsigned.sh
```

**Option B — manual commandlinetools zip:**
1. Download `commandlinetools-linux-*.zip` from developer.android.com
2. Place it at `.input/commandlinetools.zip`
3. Rerun the bootstrap or call `sdkmanager` manually

**Option C — sdkmanager already available:**
```bash
sdkmanager "build-tools;35.0.0"
```

Once aapt2 is found, the build gate is open.
