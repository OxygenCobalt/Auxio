# `t-music` vs `Auxio-TS` comparison (planning baseline)

> Status: `cbkii/t-music` is currently not accessible from this agent environment (`404`), so this comparison is a **structured placeholder** that captures what to extract and how to classify it once access is restored.

## Access status and immediate blocker
- Attempted source: `https://github.com/cbkii/t-music`
- Current result: repository not reachable from MCP (`404 Not Found`)
- Impact: no direct validation of `t-music` contracts yet
- Required follow-up: provide repo access or sanitized snapshot (see `docs/TW_ECOSYSTEM_SOURCE_MAP.md`)

## Architectural baseline differences (known now)

| Dimension | `t-music` (expected legacy/vendor-integration direction) | `Auxio-TS` (current fork direction) | Decision |
|---|---|---|---|
| Core media architecture | Likely vendor-contract-heavy/custom control bridge | Upstream Auxio playback + Media3/MediaSession flow preserved | Keep Auxio core; do not import legacy core design blindly |
| Service model | Unknown until access | `AuxioService` + `PlaybackServiceFragment` + `MusicServiceFragment` split | Reuse existing Auxio seams for adapters |
| Media session/notification | Unknown until access | Existing MediaSessionCompat + media notification lifecycle in `MediaSessionHolder` | Harden/validate first; avoid rewrites |
| TW integration style | Potential direct contract assumptions | Planned adapter facade with runtime gating | Keep TW behavior optional and isolated |
| Package identity strategy | Potential `com.tw.music` assumptions | Explicitly avoid package impersonation unless proven necessary | Maintain current package identity |

## Reclassification matrix for future `t-music` import

Use this table when corpus is available:

| `t-music` finding | Classification options | Porting decision rule |
|---|---|---|
| README/AGENTS/process guidance | Directly reusable / evidence-only | Reuse if process-level and architecture-agnostic |
| `com.tw.music.action.*` action mappings | Evidence-only / requires TS18 runtime validation | Never port blindly as code; first prove via stock-vs-Auxio traces |
| `com.tw.service` / `com.tw.service.xt` interactions | Evidence-only / requires TS18 runtime validation / avoid | Only implement if standard Android path fails and behavior is reproducible |
| Launcher/widget hooks | Requires TS18 runtime validation | Add optional launcher adapter module only after reproducible gap |
| Smali/source shims | Obsolete for Auxio architecture / explicitly avoid | Avoid direct transplant into Auxio core |
| Manifest privilege assumptions | Explicitly avoid | No privileged UID/system-signing requirements in normal app path |
| MediaControlBridge-like abstractions | Directly reusable pattern or evidence-only | Reuse only architectural pattern (interface/facade), not vendor-specific logic |
| Validation scripts/evidence tooling | Directly reusable / needs adaptation | Port script patterns if safe and license-compatible |

## What should be ported as requirements (not code)
1. Any reproducible stock behavior matrix (notification controls, media keys, launcher widget, ZLink/TLink).
2. Any precise contract claim with capture evidence (action names, expected extras, timing constraints).
3. Any safety constraints around coexistence with stock `com.tw.music`.
4. Any TS18 variant caveats and firmware-conditional behavior notes.

## What should not be ported as code
1. Package-identity replacement logic (`com.tw.music`) without hard evidence and rollback plan.
2. Privileged/system signing assumptions.
3. Decompiled/vendor app logic copied into Auxio classes.
4. Cross-cutting TS18 conditionals scattered through playback/indexing core.

## Intentional divergence points for Auxio-TS
- Preserve upstream Auxio playback/indexing architecture.
- Keep standard Android media integration as first-class contract.
- Introduce TS18-specific behavior through facade + optional contract modules only.
- Require evidence classification (`observed`/`inferred`/`hypothesis`/`requires TS18 validation`) for every TW/TWTHEME claim.

## Next step once access is available
Run a dedicated “`t-music` corpus import” PR that only:
1. indexes `t-music` docs/scripts/manifest findings,
2. fills this comparison with concrete entries,
3. updates requirements/contracts tables,
4. adds no app-feature code.
