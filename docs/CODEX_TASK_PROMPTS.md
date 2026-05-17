# Codex Task Prompts (Auxio-TS)

Use these prompts for narrow, reviewable PRs.

## 1) `t-music` corpus import
"Import `cbkii/t-music` as evidence corpus only. Extract README/AGENTS/docs/scripts/manifest/media-control references and classify each item as directly reusable, evidence-only, obsolete for Auxio architecture, requires TS18 runtime validation, or explicitly avoid. Update docs only; no app feature code."

## 2) TW ecosystem source-map expansion
"Expand `docs/TW_ECOSYSTEM_SOURCE_MAP.md` with additional public TS/TW/TWTHEME projects (GitHub/GitLab/Codeberg). For each source, record what it proves, suggests, and cannot prove. Verify source code before claims."

## 3) Stock-vs-Auxio media-session comparison
"Use captured TS18 artifacts to compare stock `com.tw.music` vs Auxio-TS media session, notification, and audio focus behavior. Update comparison docs with observed deltas and risk ranking. No speculative implementation."

## 4) TS18 launcher-widget proof-of-concept planning pass
"Design (but do not fully implement) a minimal TS18 launcher/widget adapter interface behind feature flags. Specify validation cases, stop conditions, and rollback strategy. Keep Auxio core untouched."

## 5) TW broadcast adapter skeleton
"Create a default-off TW broadcast adapter skeleton in isolated `integration/ts18` package with no-op implementation and tests/docs. No direct vendor contract logic yet; only structure + gating."

## 6) TWUtil/TWClient investigation
"Investigate TWUtil/TWClient availability and safe runtime probing strategy. Add docs/tests/diagnostics plan for optional bridge activation. No privileged assumptions and no hard dependency in core playback path."

## 7) ZLink/TLink coexistence validation
"Run/document TS18 validation scenarios for ZLink/TLink coexistence with Auxio-TS playback. Produce observed behavior matrix and recommendations for adapter hooks only if standard behavior fails."

## 8) FLAC/audio-quality validation
"Execute TS18 FLAC/local playback and audio-quality validation (16/44.1, 16/48, 24/48 if available), including focus, notifications, and resume behavior. Update requirements/runbook with results and gaps."

## 9) Package/signature/coexistence risk analysis
"Analyze package/signature/privilege constraints for TS18 launchers and stock app coexistence. Produce clear non-goals, escalation criteria, and safe experiments. No package impersonation implementation."

## 10) Release-candidate hardening
"Prepare release-candidate readiness report with validated contracts, unresolved hypotheses, rerun commands, and blocker list. Keep changes docs/tests/tooling only unless a proven low-risk fix is required."
