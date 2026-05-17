# Auxio-TS development roadmap

## Phase 0 — commit instructions and evidence tooling

Commit this package. No app behaviour changes.

## Phase 1 — baseline evidence

Use the evidence scripts to compare:

- stock `com.tw.music`;
- existing third-party players;
- upstream Auxio;
- Auxio-TS debug build.

Decision gate:

- If TS18 launcher/widgets/keys work with standard MediaSession, prioritise normal Auxio/Media3 correctness and UI fit.
- If TS18 ignores standard sessions but stock `com.tw.music` works, investigate private TW contracts.
- If only package identity matters, stop and design a safe package/bridge strategy before coding.

## Phase 2 — standard media hardening

Likely PRs:

1. Add TS18 debug screen/log export showing MediaSession state, audio focus, package/environment detection.
2. Add/verify MediaLibraryService/Android Auto compatibility on API 29.
3. Add TS18-specific validation CI/docs without vendor hooks.
4. Ensure FLAC test matrix and user-visible failure reporting.

## Phase 3 — TS18 adapter proof-of-concepts

Only if Phase 1 shows gaps.

Possible PRs:

- TS18 environment detector.
- Passive broadcast logger for TW actions while Auxio-TS runs.
- Optional compatibility broadcast emitter if stock actions are proven.
- Metadata projection shim if launcher ignores MediaSession.
- Source/focus integration shim if `com.tw.service` requires it.

## Phase 4 — package/privilege decisions

Only after proof.

Options:

- normal package remains sufficient;
- alternate package with intent aliases;
- companion bridge app;
- rooted/system install path;
- same-package replacement.

Same-package/system replacement is last resort.

## Phase 5 — polish

- TS18 landscape UI tuning.
- Large-touch controls.
- Theme compatibility.
- Launcher icon/name/channel polish.
- Release build signing and reproducibility.
