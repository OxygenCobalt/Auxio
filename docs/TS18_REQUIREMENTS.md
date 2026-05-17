# TS18 Requirements for Auxio-TS

## Scope
Requirements for making Auxio-TS a credible companion/replacement candidate for stock `com.tw.music` while preserving maintainability.

## 1) Hard requirements (must meet)

### Android-native media correctness
1. Correct media session lifecycle and transport control behavior.
2. Foreground playback service + stable media notification controls.
3. Reliable media-button handling (headset/hardware key path where available).
4. Correct audio-focus request/abandon/interruption behavior.
5. Local-library scanning and playback stability.
6. FLAC playback support validation for TS18 target.
7. Android Auto/media-browser compatibility validation using existing Auxio service model.

### TS18 program requirements
1. Stock `com.tw.music` baseline capture before compatibility claims.
2. Reproducible stock-vs-Auxio evidence corpus for sessions/notifications/audio focus.
3. TS18-specific logic isolated behind adapter/facade boundaries.
4. No package impersonation (`com.tw.music`) and no privileged/system UID dependency in default install path.
5. Every TW/TWTHEME claim explicitly labeled observed/inferred/hypothesis/requires-validation.

## 2) Preferred requirements (target)
1. Steering-wheel/hardware media keys work through standard dispatch path.
2. Launcher/home music widget reflects Auxio-TS metadata/controls.
3. Coexistence with ZLink/TLink media/control flows.
4. Smooth sleep/resume and foreground/background continuity.
5. Coexistence with stock app installation (no destructive replacement required).

## 3) Experimental requirements (evidence-gated)
1. Optional TW broadcast adapter (only if standard path fails).
2. Optional TWUtil/TWClient bridge (only if class availability + behavior are proven).
3. Optional launcher-specific metadata bridge.
4. Optional TWTHEME/resource compatibility layer.

## 4) Non-goals
1. Ground-up rewrite of Auxio into vendor app clone.
2. Blind copy of external TW/TOPWAY code or decompiled logic.
3. Forcing package/signature identity matching to stock app.
4. Declaring full TS18 compatibility without hardware evidence.

## 5) Unresolved requirements (need evidence)
1. Whether launcher widget consumes standard MediaSession metadata from third-party apps.
2. Whether any `com.tw.music.action.*` intents are truly required.
3. Whether `com.tw.service` mediates behavior that affects third-party media apps.
4. Whether ZLink/TLink override/suppress third-party metadata/control channels.
5. Which sleep/resume transitions are controlled by Android vs vendor stack.

## 6) Assumptions requiring TS18 validation
1. TS18 profile in `diagnostics/redacted/ts18_device_profile.json` represents target class but not all firmware variants.
2. Standard Android APIs should succeed for core playback controls before private hooks.
3. TW private contracts may exist but are unstable and variant-specific.

## 7) Risk requirements (must be tracked per PR)
1. **Package/signature risk:** document if any experiment interacts with signature-sensitive launchers.
2. **Privilege risk:** reject any requirement needing system UID or privileged permission.
3. **Compatibility risk:** maintain stock Auxio behavior for non-TS18 users.
4. **Evidence risk:** no claim should exceed the quality of collected evidence.
