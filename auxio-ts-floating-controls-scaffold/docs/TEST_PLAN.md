# Test Plan

## Emulator / local developer tests

1. Build topway variant.
2. Launch Auxio-TS.
3. Open overlay permission screen.
4. Confirm permission flow handles allow/deny/cancel.
5. Start overlay.
6. Confirm only one overlay is attached.
7. Drag overlay; restart service; confirm position persists.
8. Press previous/play-pause/next and verify playback bridge calls.
9. Open Auxio activity; verify overlay hides.
10. Background Auxio; verify overlay reappears when setting is enabled.
11. Disable setting; verify overlay is removed and service stops.

## TS18/head-unit tests

1. Install topway release APK.
2. Grant Display over other apps.
3. Start music in Auxio-TS.
4. Press Home / open launcher.
5. Confirm overlay remains visible.
6. Open navigation app.
7. Confirm overlay remains visible and movable.
8. Open radio app.
9. Confirm overlay remains visible unless radio/fullscreen layer blocks overlays.
10. Press previous/play-pause/next.
11. Confirm Auxio-TS responds, not stock music app.
12. Open Auxio-TS.
13. Confirm overlay hides.
14. Press Home.
15. Confirm overlay returns.
16. Reboot or ACC off/on.
17. Confirm no duplicate overlays and setting survives.
18. Test reverse camera; confirm overlay does not create unsafe obstruction.
19. Test Bluetooth call, CarPlay, radio, steering wheel buttons.
20. Disable feature and confirm stock behaviour returns.

## Regression tests

- MediaSession metadata still reaches system/launcher controls.
- Standard notification media controls still work.
- Existing Topway/DoFun compatibility flavour still builds.
- App startup does not request overlay permission unexpectedly unless user enables feature.
- No ANR or overlay crash when WindowManager rejects add/update/remove.
