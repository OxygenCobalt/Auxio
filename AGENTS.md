# AGENTS.md — Auxio-TS coding authority

## 1) Project purpose
Auxio-TS is a TS18-focused, maintainable fork of Auxio. The goal is **TS18-native usability** while preserving upstream Auxio quality and Android media correctness.

This is not a blind clone of stock `com.tw.music`. Build on standard Android + Media3 first, then add narrowly scoped TS18 adapters only when evidence proves they are needed.

## 2) Authoritative TS18 device context
Primary evidence target is `diagnostics/redacted/ts18_device_profile.json`:
- Observed Android 10 (API 29), Unisoc/SPRD `uis8581a2h10`, model `s9863a1h10_Natv`.
- Observed installed TW stack including `com.tw.music`, `com.tw.service`, `com.tw.service.xt`, `com.tw.eq`, `com.tw.bt`, `com.tw.radio`, etc.
- Observed TWTHEME assets under `/system/etc/theme/default/...` including `Sub/MusicTheme.apk`.
- Observed `persist.phone_connect_app=com.zjinnova.zlink`.
- Observed in captured diagnostics: `com.tw.service` audio-focus ownership; no active media session at capture moment.

When uncertain, classify claims as:
- **Observed** (in repo diagnostics/runtime captures),
- **Inferred** (reasonable but unproven),
- **Hypothesis** (weak/unverified),
- **Requires TS18 validation** (must be tested on hardware).

## 3) Development priorities (in order)
1. Keep upstream Auxio core stable and maintainable.
2. Ensure Android-native media behaviour: MediaSession, notification, media keys, audio focus, background playback.
3. Validate Android Auto/MediaBrowser/MediaLibrary behaviour supported by current Auxio baseline.
4. Add TS18 integration adapters behind feature flags and clear boundaries.
5. Validate FLAC/local-library behaviour on TS18.

## 4) Allowed work
- Documentation, runbooks, scripts, and evidence tooling.
- Small, reviewable refactors that preserve behaviour.
- Android/Media3 correctness fixes supported by official docs.
- TS18-specific adapter scaffolding isolated in dedicated packages/modules.
- Test and logging improvements that do not require privileged/system capabilities.

## 5) Forbidden / high-risk work without explicit human approval
- Replacing package identity with `com.tw.music`.
- Requiring `android.uid.system`, privileged/system permissions, or platform signing.
- Committing proprietary APKs, firmware blobs, raw unredacted diagnostics, serials, personal data.
- Broad rewrites of playback/library/UI not tied to a validated TS18 requirement.
- Scattering TS18 conditionals through core playback and indexing code.
- Claiming a TS18 integration works without reproducible evidence.

## 6) One-variable-at-a-time rule
Each PR should change one primary variable:
- example: “MediaSession state visibility only”, or
- “TS18 environment detector only”, or
- “launcher compatibility experiment only”.

Do not combine major behaviour changes in one PR.

## 7) Evidence requirements
Every TS18-facing change must include:
- exact command(s) and/or manual steps,
- expected output artifacts,
- observed results,
- explicit gap list.

If a behaviour is not proven on TS18 hardware, mark it **requires TS18 validation**.

## 8) Validation requirements
At minimum run (or document why unavailable):
- `./gradlew tasks`
- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew lint`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`

For TS18 runtime work, use `docs/TS18_VALIDATION_RUNBOOK.md` and scripts under `scripts/`.

## 9) Commit / PR expectations
- Small, evidence-driven commits.
- Commit message: scope + intent (e.g., `docs(ts18): tighten native contract classification`).
- PR must include:
  - summary,
  - files changed,
  - behaviour changed,
  - TS18 assumptions used,
  - validation commands + results,
  - remaining TS18 runtime checks,
  - risks/blockers,
  - next recommended task.

## 10) Android / Media3 coding expectations
- Prefer official Android/AndroidX Media3 guidance.
- Preserve correct `MediaSession` ownership and lifecycle.
- Preserve notification transport controls and media-button routing.
- Preserve audio focus discipline and noisy-device handling.
- Keep Auto/media browsing behaviour standards-compliant where supported.

## 11) TS18 / TWTHEME caution areas
Treat TW/TWTHEME integrations as opt-in adapters:
- launcher widget and home-card metadata paths,
- steering-wheel key routing,
- `com.tw.service` / `com.tw.service.xt` interactions,
- ZLink/TLink metadata/control coexistence,
- theme/resource coupling via TWTHEME.

Implement only after evidence shows standard Android APIs are insufficient.

## 12) Diagnostics handling policy
- Diagnostics in `diagnostics/redacted/` are reference evidence, not truth for all TS18 variants.
- Never commit raw private captures.
- Prefer redacted summaries and reproducible scripts.
- Keep observed vs inferred clearly separated in docs and code comments.

## 13) Stop conditions (report, do not code)
Stop and escalate if a task requires:
- privileged/system signing or `android.uid.system`,
- package impersonation/replacement without runtime proof and rollback plan,
- unverified TW-native contracts as implementation blockers,
- changes likely to break Android-native MediaSession/notification behaviour.
