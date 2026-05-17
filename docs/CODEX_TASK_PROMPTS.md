# Codex Task Prompts (Auxio-TS)

Use these prompts as conservative, agent-ready starting points.

## 1) Repo audit prompt
"Audit Auxio-TS for TS18 readiness without broad feature coding. Inspect build, manifest, media session/service code, notification/media-button path, Auto/media-library support, docs/scripts/workflows. Classify findings as observed/inferred/hypothesis/requires TS18 validation. Propose small PR-safe next steps only."

## 2) Baseline build fix prompt
"Run `./gradlew tasks assembleDebug test lint` and report failures precisely. Apply minimal, maintainable fixes that preserve upstream Auxio behaviour. Do not add TS18 private hooks or package/permission changes."

## 3) TS18 diagnostics review prompt
"Review diagnostics under `diagnostics/redacted/` plus TS18 docs. Produce a contract table separating proven vs inferred claims. Update docs to remove overstatements and add explicit runtime validation steps."

## 4) MediaSession proof-of-concept prompt
"Implement/verify a no-risk MediaSession observability enhancement (logging/debug export) without changing playback semantics. Demonstrate active session state transitions and command handling visibility for TS18 validation runs."

## 5) Notification/media-button validation prompt
"Validate and harden Android-native media notification + media-button routing. Keep changes standard Android/Media3-compliant. Add tests/docs/runbook deltas only as needed. No TW-private integration in this PR."

## 6) TS18 adapter skeleton prompt
"Create a minimal TS18 adapter/facade skeleton (default no-op) in isolated package paths. Wire via feature flags/runtime detection points only. Do not alter core queue/playback logic."

## 7) Launcher/TWTHEME contract investigation prompt
"Using fresh TS18 evidence, determine whether launcher/TWTHEME behaviour requires private contracts. Compare stock `com.tw.music`, third-party app(s), and Auxio-TS. Provide recommendation and stop conditions before coding private hooks."

## 8) FLAC playback validation prompt
"Validate FLAC scenarios (16/44.1, 16/48, 24/48) on TS18 with evidence capture. Identify regressions and propose minimal fixes. Preserve upstream audio path unless clear device-specific issue is proven."

## 9) Release-candidate hardening prompt
"Prepare release-candidate readiness report with open risks, validated contracts, unresolved hypotheses, and exact rerun commands. Avoid invasive refactors; prioritize reproducibility and documentation quality."
