# TS18 Requirements for Auxio-TS

## 1) Hard requirements

### Android-native media correctness
1. Correct MediaSession lifecycle/state/metadata publication.
2. Foreground playback service and notification transport controls.
3. Reliable media-button handling (standard Android path first).
4. Correct audio-focus request/abandon/interruption behavior.
5. Stable local-library scanning and playback.
6. FLAC/local playback validation on TS18.
7. Android Auto/media-browser compatibility validation.

### TS18 program requirements
1. Stock `com.tw.music` baseline evidence before parity claims.
2. Stock-vs-Auxio evidence corpus for sessions/notification/focus/widget behavior.
3. TS18 logic isolated behind adapter/facade boundaries.
4. No package impersonation and no privileged UID dependency.
5. Every TW/TWTHEME claim labeled with confidence + porting decision.

## 2) Preferred requirements
1. Steering-wheel/media-key behavior works via standard path; document gaps.
2. Launcher/home widget metadata/control parity where feasible.
3. ZLink/TLink coexistence without metadata/control breakage.
4. Reliable sleep/resume and navigation-mixing behavior.
5. Safe coexistence with stock `com.tw.music` installed.

## 3) Experimental requirements (default-off)
1. TW broadcast adapter for `com.tw.music.action.*` parity gaps.
2. TW service adapter for `com.tw.service.xt`-related gaps.
3. Launcher/widget adapter for non-standard launcher contracts.
4. TWUtil/TWClient bridge only if runtime-proven available and necessary.
5. TWTHEME compatibility notes/probes (not hard dependency).

## 4) Non-goals
1. Turning Auxio-TS into a smali-first clone of stock app.
2. Adopting `com.tw.music` package/shared UID/signature model.
3. Copying proprietary/decompiled implementation into Auxio core.
4. Declaring broad TS18 compatibility without hardware evidence.

## 5) Unresolved requirements
1. Are `com.tw.music.action.*` broadcasts required for TS18 launcher/hardware parity with third-party apps?
2. Does `com.tw.service`/`com.tw.service.xt` materially affect third-party media focus/control?
3. Which launcher/TWTHEME paths require non-standard metadata integration?
4. How do ZLink/TLink alter metadata/control ownership during active projection?
5. Which sleep/resume behaviors are Android-native vs TW vendor specific?

## 6) Assumptions requiring TS18 validation
1. Snapshot stock contracts are representative of this device firmware.
2. Standard Android media path should satisfy most requirements.
3. Any required TW-private contract can be isolated to optional modules.

## 7) Risk requirements (track in every TS18 PR)
1. Package/signature/privilege risk.
2. Core-behavior regression risk outside TS18 scenarios.
3. Evidence-quality risk (claim exceeds proof).
4. Vendor-lock-in risk from premature TW-private assumptions.


## 8) Head-unit UI/UX requirements (Auxio-TS scope)
1. Provide head-unit settings for landscape mode, driver side, large controls, and minimal album-art control.
2. Quick Picks are additive shortcuts only and must not remove deep browse/navigation.
3. Metadata chips are optional shortcuts only.
4. Allowed chip categories: genres, decades, folders, recently added, and favourites only when first-class favourites support exists in-app.
5. Forbidden chip categories: file type, file size, bitrate, codec, sample rate, storage size, and technical file-property filters.
6. Queue management and advanced navigation features must remain available.
7. No driving/parked restrictions are implemented in Auxio-TS UI scope.
8. UI/UX improvements do not prove TS18 native/TWTHEME compatibility; runtime validation is still required.


Current branch note: Favourites quick-pick/chip remains intentionally omitted/disabled in Auxio-TS head-unit UI because no first-class favourites/starred model + destination is exposed in the current app layer.
