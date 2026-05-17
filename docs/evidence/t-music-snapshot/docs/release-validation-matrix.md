# Release Validation Matrix — TS18 Android 13 (com.tw.music)

Last updated: 2026-05-02 (UTC)
Target device baseline: TS18 4GB/64GB, 1280×720 landscape, Android 13 vendor firmware.

## Gate policy

- **P0 rows are release-blocking**: all must pass before release branch cut.
- **P1 rows are strongly recommended**: failures must be documented with risk sign-off.

## Matrix

| Pri | Area | Test case | Preconditions | Steps | Expected result | Evidence |
|---|---|---|---|---|---|---|
| P0 | Build/install | Unsigned build reproducibility | Toolchain configured | Run `bash scripts/02_build_unsigned.sh` then `bash scripts/01_refresh_reports.sh` | Build exits 0; unsigned APK exists | Command output + artifact paths |
| P0 | Signing | Shared UID signing compatibility | Correct keystore for installed package lineage | Run signing flow from `docs/manual-steps/02-release-signing.md` | Signed APK verifies and can install/update without signature mismatch | `apksigner verify` output + install result |
| P0 | Playback controls | Play/pause/next/prev command handling | Media library has playable tracks | Trigger UI controls and hardware/vendor actions (`com.tw.music.action.*`) | All actions dispatch and execute correctly | Manual test notes + logs |
| P0 | MediaSession | PlaybackState transitions | Active playback session | Exercise play, pause, seek, stop, error path | State enum/position/speed/actions remain coherent | `dumpsys media_session` snapshot + notes |
| P0 | Metadata | Track metadata publication | Multiple tracks with tags/artwork | Change tracks during playback | Title/artist/album/duration/artwork update consistently | Screenshot of system media card + notes |
| P0 | Notification | MediaStyle action correctness | Foreground service active | Use notification buttons for prev/play-pause/next | Buttons map to correct behavior and remain responsive | Screenshot + action notes |
| P0 | Widget | Widget render + command actions | Widget placed on launcher | Verify widget updates and controls | Widget renders and actions control playback | Screenshot + notes |
| P1 | TLink coexistence | Session continuity across suspend/resume | TLink/CarPlay/AA bridge active | Suspend/resume app via bridge scenarios | Session state remains valid and recovers correctly | Manual notes + logs |
| P1 | Regression | Vendor token integrity check | Repo clean | Run `bash scripts/08_verify_vendor_tokens.sh` | No unintended vendor token drift | Command output |
| P1 | Pixel smoke (dev-only) | Pixel 9a compat no-uid launch smoke | Pixel 9a / Android 16 attached via adb | Build with `bash scripts/10_build_pixel9a_compat.sh`, then run `bash scripts/11_pixel_validation_harness.sh` | Installs, starts, no immediate `android.tw.john.*` class resolution crash in launch window | Harness log + logcat excerpt |

## Execution notes

- Signing policy note: preserving original vendor signature is not required for this project; new debug/release signatures are acceptable. Install-over-existing behavior still depends on installed package lineage and `sharedUserId` compatibility.


- Record each row as PASS/FAIL with date and tester initials in release notes.
- If a P0 row fails, do not cut release branch.
- Keep vendor boundary strings/components/actions unchanged unless explicitly scoped.
