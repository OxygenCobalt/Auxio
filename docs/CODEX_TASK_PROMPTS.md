# Codex Task Prompts (Auxio-TS)

## 1) t-music snapshot corpus import refinement
"Analyze `docs/evidence/t-music-snapshot/` only. Extract manifest/actions/services/theme/widget/media-session findings and classify each as: directly reusable requirement, reusable validation idea, evidence only, obsolete for Auxio architecture, requires TS18 runtime validation, unsafe to port, or explicitly avoid. Update docs only."

## 2) TW ecosystem source-map expansion
"Expand `docs/TW_ECOSYSTEM_SOURCE_MAP.md` using verified public TS/TW/TWTHEME sources. For each source, record what it proves/suggests/cannot prove and how it changes Auxio-TS planning."

## 3) stock-vs-Auxio media-session comparison
"Using TS18 capture artifacts, update parity docs with observed deltas for session owner/state/actions/metadata/notification/audio focus. No feature implementation."

## 4) TS18 launcher/widget proof-of-concept planning
"Design a default-off launcher/widget adapter interface and validation matrix; keep Auxio core untouched; produce stop conditions and rollback criteria."

## 5) TW broadcast adapter skeleton
"Create a default-off TS18 TW broadcast adapter skeleton and contract registry wiring in isolated integration package; no vendor-private command logic enabled yet."

## 6) TWUtil/TWClient investigation
"Design safe runtime probing and fallback strategy for TWUtil/TWClient availability, with diagnostics-first behavior and no hard dependency in core playback path."

## 7) ZLink/TLink validation pass
"Run/update runbook scenarios for ZLink/TLink idle-vs-active coexistence and publish evidence-labeled findings and unresolved risks."

## 8) FLAC/audio-quality validation
"Execute TS18 FLAC/local playback matrix and document focus, metadata, notification, and resume behavior with evidence labels."

## 9) package/signature/coexistence analysis
"Produce a package/signature/privilege risk matrix for stock coexistence vs Auxio-TS, with explicit non-goals and escalation criteria."

## 10) release-candidate hardening
"Prepare release-readiness report: validated contracts, unresolved hypotheses, rerun command results, remaining TS18 runtime checks, and next risk-reduction PR."
