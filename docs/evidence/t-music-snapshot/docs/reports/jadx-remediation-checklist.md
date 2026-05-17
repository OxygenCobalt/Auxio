# JADX Remediation Checklist (Phase 0 execution)

Last updated: 2026-05-02 (UTC)

Scope policy:
- Prioritize app/vendor-critical paths first.
- Third-party/library decompile failures (antlr/cpdetector/support libs) are tracked but not immediately annotated in smali unless they impact active migration work.

## Priority A — app/vendor-critical

- [x] `com/p060tw/music/MusicActivity.java:665` -> `app/apktool/smali_classes3/com/tw/music/MusicActivity.smali` class-level guard comment added; method-level pinpoint refinement pending.
- [x] `com/eckom/xtlibrary/p020b/p037f/p041d/C0596O.java:34` -> mapped to `app/apktool/smali_classes3/com/eckom/xtlibrary/b/f/d/O.smali`; guard comment added.
- [x] `tv/danmaku/ijk/media/player/IjkMediaPlayer.java:918` -> class-level guard comment added; active-path impact analysis still pending.
- [x] `tv/danmaku/ijk/media/player/p069tw/MeasureHelper.java:46` -> class-level guard comment added; UI/layout impact analysis still pending.

## Priority B — known non-app-third-party buckets (defer unless required)

- [ ] `antlr/*` decompile errors tracked (non-app-core library code).
- [ ] `cpdetector/*` decompile errors tracked (utility/lib code).
- [ ] `android/support/*` scattered decompile errors tracked (framework support libs).
- [ ] `com/p060tw/music/R.java` generation errors tracked (resource class noise; not canonical editable logic surface).

## Evidence mapping status

- [x] Created checklist artifact and triage policy.
- [ ] Each Priority A row linked to exact smali method range + completion state (class-level guard comments done; per-method pinpoint still open).
- [ ] Blueprint/status/deobf notes synced after each Priority A closure.


- [x] Vendor hook owner baseline map created: `docs/reports/vendor-hook-owners.md`.
