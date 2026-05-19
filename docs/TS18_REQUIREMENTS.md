# TS18 Requirements for Auxio-TS (source-led)

## Phase 1 implementation requirements (product code)
1. Standards-compliant MediaSession behavior.
2. Standards-compliant MediaLibraryService / Android Auto browsing.
3. Reliable media notification transport controls.
4. Reliable media button support through standard Android APIs.
5. Audio focus correctness (gain/loss/duck/pause/recover).
6. Head-unit UI suitability (landscape readability, large touch targets, persistent playback affordances).
7. Stable local library scanning/playback including FLAC baseline matrix.
8. Steering-wheel behavior via standard media key/controller pathway where possible.

## Validation requirements (acceptance testing)
1. Validate TS18 launcher/TWTHEME surfaces for metadata/control visibility.
2. Validate ZLink/TLink coexistence with focus/session stability.
3. Validate sleep/resume and navigation-mixing behavior.
4. Validate stock coexistence and default-player switching.

## Explicit constraints
1. No package impersonation (`com.tw.music`).
2. No shared/system UID requirements.
3. No copied proprietary/decompiled implementation.
4. No speculative runtime TW probe framework inside app code.
5. No vendor service binding unless a concrete feature is explicitly designed and justified.
