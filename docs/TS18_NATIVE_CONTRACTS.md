# TS18 Native Contracts Catalog

This document separates proven facts from speculative claims so TS18 work stays auditable.

## Classification legend
- **Observed on user TS18:** directly present in `diagnostics/redacted/ts18_device_profile.json`.
- **Observed in `t-music`:** currently unavailable (repo access blocked), placeholder pending import.
- **Observed in public TW/TS projects:** explicit in public source code.
- **Inferred from manuals/community:** plausible but not source-level proof.
- **Unproven hypothesis:** weak claim, keep disabled.
- **Rejected/out-of-scope:** conflicts with project guardrails.

## Contract table

| Contract / identifier | Current class | Evidence source | Expected behavior | Relevance to Auxio-TS | Implementation strategy | Validation method | Risk |
|---|---|---|---|---|---|---|---|
| `com.tw.music` package presence | Observed on user TS18 | `diagnostics/redacted/ts18_device_profile.json` | Stock music app is preinstalled/system-integrated | Baseline comparator app | Use only for stock-vs-Auxio behavior comparison harness | Capture stock playback traces + Auxio traces | Medium |
| `com.tw.service` | Observed on user TS18 | same diagnostics (audio focus/volume ownership) | TW service likely participates in source/focus policy | High for focus/mixing analysis | No direct integration initially; observe and compare | `dumpsys audio`, logcat, A/B playback scenarios | High |
| `com.tw.service.xt` | Observed on user TS18 | diagnostics package list | Companion TW service exists | Medium | Treat as unknown vendor component until proven | package/intent/log observation | Medium |
| `com.tw.radio` | Observed on user TS18 | diagnostics package list | OEM radio app present | Comparator only | Keep as reference app, no coupling | stock radio behavior capture | Low |
| `com.tw.eq` | Observed on user TS18 | diagnostics package list | OEM EQ stack present | Medium for audio-effects coexistence | Validate coexistence; no direct dependency | playback + EQ/manual checks | Medium |
| `com.tw.carchoose` | Inferred from ecosystem naming | user request + common TW package naming | Possible source-switch/launcher role | Unknown | investigate-only | package scan and intent resolution on device | Medium |
| `TWTHEME` paths (`/system/etc/theme/...`) | Observed on user TS18 | diagnostics theme file list | Theme resources include app-specific assets (`MusicTheme.apk`) | High for launcher/theme compatibility hypothesis | no hard binding unless proven needed | compare launcher behavior with/without stock app active | High |
| `MusicTheme.apk` | Observed on user TS18 | diagnostics theme list | OEM music skin/resource pack exists | Medium | evidence-only until contract known | filesystem + runtime UI correlation | Medium |
| `com.tw.music.action.cmd` | Unproven hypothesis | requested investigation target | may command stock music behavior | Potential if standard controls fail | keep disabled until observed in runtime/static references | explicit broadcast tracing and outcome logging | High |
| `com.tw.music.action.prev` | Unproven hypothesis | requested target | previous-track transport intent | same as above | same as above | same as above | High |
| `com.tw.music.action.next` | Unproven hypothesis | requested target | next-track transport intent | same as above | same as above | same as above | High |
| `com.tw.music.action.pp` | Unproven hypothesis | requested target | play/pause transport intent | same as above | same as above | same as above | High |
| `android.tw.john.TWUtil` | Observed in public TW/TS projects | `asb72/dvd-bt`, `d51x/KaierUtils`, `ivvlev/CarRadio` | low-level HU bridge API on some firmware | Candidate optional adapter dependency | runtime feature probe + optional bridge module only | reflection/class-availability probe on TS18; no-op fallback | High |
| `android.tw.john.TWClient` | Observed in public TW/TS projects | `ivvlev/CarRadio` | companion TW API for commands/channels | Candidate optional adapter | isolate in optional contract module | same as above + guarded command tests | High |
| `TWService` naming | Inferred from community/project patterns | public HU codebases | vendor service endpoint might exist | Unknown | investigate-only | package manager + binder/service dumps | Medium |
| ZLink package (`com.zjinnova.zlink`) | Observed on user TS18 | diagnostics (`persist.phone_connect_app`) | phone-link stack is active in ecosystem | High for metadata/control coexistence | no direct hook initially; observe interactions | ZLink active/inactive A/B playback runbook steps | High |
| TLink-related packages | Inferred from ecosystem | user requirement + head-unit conventions | alternate phone-link implementation may coexist | Medium | detect if installed, include in matrix | package scan + scenario tests | Medium |
| Launcher media widget plugin behavior | Inferred from manuals/community | launcher forums/manuals; not proven on this device | widget may consume non-standard metadata/contracts | High | add optional launcher adapter only if standard MediaSession fails | stock vs Auxio widget comparison captures | High |

## `t-music` contract import placeholders
`cbkii/t-music` is currently inaccessible in this environment. Once access is restored, add rows for:
- exact `com.tw.music.action.*` usage,
- any media-button/media-session bridge behaviors,
- any launcher widget or TW service assumptions,
- and classify each row with the same risk/validation rubric.

## Rejected / out-of-scope contracts (for now)
1. Package impersonation (`com.tw.music`) as primary strategy.
2. privileged/system UID requirements.
3. direct copy of undocumented vendor smali paths into Auxio core.
4. declaring TS18 compatibility without reproducible runtime evidence.
